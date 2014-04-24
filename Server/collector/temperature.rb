# Clase para obtener la temperatura del sistema
# se basa en la llamada /opt/vc/bin/vcgencmd measure_temp

class Temperature

	attr_reader :temperature

	def initialize()

		regex = /\w*=([0-9\.]*)/

		# Ejecutamos el comando aqui por ser unico. En el caso de CPU y Memory para no duplicarlo,
		# ejecutamos el comando top desde app.rb

		# Obtenemos la temperatura
		@result = `/opt/vc/bin/vcgencmd measure_temp`.match regex

		if !@result.nil? then

			# Aplicamos los valores
			@temperature = @result[1].to_f
		else
			@temperature = -1
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
end
