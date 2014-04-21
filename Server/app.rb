# Iniciamos
require 'sinatra'
require 'json'

# Obtenemos las clases
require_relative("collector/cpu.rb")
require_relative("collector/memory.rb")

# Raspbian
#@regexCpu = /%Cpu\(s\):\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)/;
#@regexMem = /KiB Mem:\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)/;
#@regexMemswap = /KiB Swap:\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)/;

get '/' do
  'Hello world!'
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

	cpu = Cpu.new(output);
	memory = Memory.new(output);

	if !cpu.isError && !memory.isError

		if params[:format] != nil && params[:format] == 'json' then

			{:status => true, :result => {
				:memory => {
					:physical => {
						:used => memory.used(),
						:buffered => memory.buffered(),
						:percent => memory.getPercent(),
						:free => memory.free()
					}
				},
				:cpu => {
					:user => cpu.user(),
					:system => cpu.system(),
					:free => cpu.free()
				}
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