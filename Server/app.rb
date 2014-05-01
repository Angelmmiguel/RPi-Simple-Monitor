# Iniciamos
require 'sinatra'
require 'json'
require 'time_diff'

# Obtenemos las clases
require_relative("collector/cpu.rb")
require_relative("collector/memory.rb")
require_relative("collector/temperature.rb")

# Raspbian
#@regexCpu = /%Cpu\(s\):\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)/;
#@regexMem = /KiB Mem:\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)/;
#@regexMemswap = /KiB Swap:\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)/;

# Cambiamos el puerto y lo iniciamos en produccion
set :port, 61456
set :environment, :production

get '/' do
  'Hello RPi!'
end

get '/cpu.?:format?' do 

	# Obtenemos la salida del comando
	#output = `top -l 1`
	output = `top -n 1 -b`

	# Iniciamos el objet CPU
	cpu = Cpu.new(output)

	if !cpu.isError 

		if params[:format] != nil && params[:format] == 'json' then

			{:status => true, :result => {
				:cpu => {
					:user => cpu.user(),
					:system => cpu.system(),
					:free => cpu.free()
				}
			}}.to_json

		else 
			'<p>CPU user: ' + cpu.user() + '%</p>' +
			'<p>CPU system: ' + cpu.system() + '%</p>' +
			'<p>CPU free: ' + cpu.free() + '%</p>'
		end
	else
		if params[:format] != nil && params[:format] == 'json' then
			
			{:status => false, :result => {}, :reason => 'No hemos podido obtener los datos'}.to_json

		else 
			'No hemos podido obtener datos' 
		end
	end

end

get '/memory.?:format?' do 

	# Obtenemos la salida del comando
	output = `top -n 1 -b`

	# Iniciamos la clase memoria
	memory = Memory.new(output)

	if !memory.isError 

		if params[:format] != nil && params[:format] == 'json' then

			{:status => true, :result => {
				:memory => {
					:physical => {
						:used => memory.used(),
						:percent => memory.getPercent(),
						:free => memory.free()
					}
				}
			}}.to_json

		else 
			'<p>Memory used: ' + memory.used() + '%</p>' +
			'<p>Memory free: ' + memory.free() + '%</p>' 
		end
	else
		if params[:format] != nil && params[:format] == 'json' then
			
			{:status => false, :result => {}, :reason => 'No hemos podido obtener los datos'}.to_json

		else 
			'No hemos podido obtener datos' 
		end
	end

end

get '/stats.?:format?' do

	output = `top -n 1 -b`;
	name = `hostname`;

	cpu = Cpu.new(output);
	memory = Memory.new(output);
	temperature = Temperature.new();

	if !cpu.isError && !memory.isError

		if params[:format] != nil && params[:format] == 'json' then

			uptime = getUptime

			if uptime.nil?
				uptime = { :error => 1 }
			else 
				uptime[:error] = 0
			end

			{:status => true, :result => {
				:name => name,
				:memory => {
					:physical => {
						:used => memory.used(),
						:buffered => memory.buffered(),
						:percent => memory.getPercent(),
						:free => memory.free()
					},
					:swap => {
						:used => memory.usedSwap(),
						:cached => memory.cachedSwap(),
						:percent => memory.getPercentSwap(),
						:free => memory.freeSwap()
					}
				},
				:cpu => {
					:user => cpu.user(),
					:system => cpu.system(),
					:free => cpu.free(),
					:other => cpu.other()
				},
				:temperature => temperature.temperature(),
				:uptime => uptime
			}}.to_json

		else 
			'HTML' 
		end
	else
		if params[:format] != nil && params[:format] == 'json' then
			
			{:status => false, :result => {}, :reason => 'No hemos podido obtener los datos'}.to_json

		else 
			'No hemos podido obtener datos' 
		end
	end
end

def getUptime

	# Regex para obtener el uptime y valor
	reg = /\s*(\d*\.\d*)/
	out = `cat /proc/uptime`

	# Obtenemos el uptime
	res = out.match reg

	if res.nil? then

		return nil

	else 

		uptime = Time.diff(Time.at(0), Time.at(res[1].to_f));

		return uptime

	end

end
