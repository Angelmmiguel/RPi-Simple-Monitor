# Clase para obtener los datos de la memoria
# Se basa en la llamada a top -n 1

class Memory 

	attr_reader :used, :free, :total, :buffered

	def initialize(top_output)
		# Regex para obtener los datos
		#regex = /PhysMem:\s*(\d*)M[\s\w]*\([\s\w]*\),\s*(\d*)M/;
		regex = /KiB Mem:\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)[\s\w]*,\s*(\d*)/;

		@result = top_output.match regex

		if !@result.nil? then

			@total = @result[1].to_f
			@used = @result[2].to_f
			@free = @result[3].to_f
			@buffered = @result[4].to_f
		else
			@total = -1
			@used = -1
			@free = -1
			@buffered = -1
		end
	end

	# Devuelve el porcentaje usado
	def getPercent()
		return ((@used / @total) * 100).round(2);
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