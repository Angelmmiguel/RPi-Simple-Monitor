# Clase para obtener los datos de la CPU
# Se basa en la llamada a top -n 1

class Cpu

	attr_reader :user, :free, :system, :other

	def initialize(top_output)
		# Regex para obtener los datos
		# FIXME (este regex es para MAC)
		#regex = /CPU usage:\s*([0-9\.]*)%[\s\w]*,\s*([0-9\.]*)%[\s\w]*,\s*([0-9\.]*)%[\s\w]*/;
		regex = /%Cpu\(s\):\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)/;
		regexCpuTop = /([0-9\,]+)\s*[0-9\,]*\s*\d*:\d*.\d*\s*top/;

		# Obtenemos los resultados generales
		@result = top_output.match regex
		# Obtenemos la CPU consumida por el comando top
		cpuTop = top_output.match regexCpuTop

		if !@result.nil? then

			if cpuTop.nil? then
				# Valor tipico en RPi por si hay error
				cpuTop = 10;
			else 
				# Valor de top en el instante
				cpuTop = cpuTop[1].to_f
			end

			# Aplicamos los valores y el exceso del comando top
			@user = @result[1].to_f - cpuTop
			@system = @result[2].to_f
			@free = @result[4].to_f + cpuTop
			@other = 100 - @user - @free - @system
		else
			@user = -1
			@system = -1
			@free = -1
			@other = -1
		end
	end

	# Devuelve el porcentaje usado
	def getPercent()
		return (((@user + @system + @other) / (@user + @free + @system)) * 100).round(2);
	end

	# Comprobamos si ha habido error
	def isError()

		if !@result.nil? then
			return false
		else 
			return true
		end
	end

end