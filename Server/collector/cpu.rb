# Clase para obtener los datos de la CPU
# Se basa en la llamada a top -n 1

class Cpu

	attr_reader :user, :free, :system

	def initialize(top_output)
		# Regex para obtener los datos
		# FIXME (este regex es para MAC)
		#regex = /CPU usage:\s*([0-9\.]*)%[\s\w]*,\s*([0-9\.]*)%[\s\w]*,\s*([0-9\.]*)%[\s\w]*/;
		regex = /%Cpu\(s\):\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)[\s\w]*,\s*([0-9\,]*)/;

		@result = top_output.match regex

		if !@result.nil? then

			@user = @result[1].to_f
			@system = @result[2].to_f
			@free = @result[4].to_f
		else
			@user = -1
			@system = -1
			@free = -1
		end
	end

	# Devuelve el porcentaje usado
	def getPercent()
		return (((@user + @system) / (@user + @free + @system)) * 100).round(2);
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