RPi-Simple-Monitor
==================

Un pequeño servidor para obtener el estado de tu RaspberryPi.

## Requisitos

### Servidor

El servidor está en escrito en [Ruby](www.ruby-lang.org/es "Ruby), y utiliza las gemas:

-	[Sinatra](www.sinatrarb.com/‎ "Sinatra")
-	[Thin](http://code.macournoyer.com/thin/ "Ruby")

Por ello lo primero es instalar Ruby en tu RaspberryPi. Lo primero es instalar sus dependencias, para ello nos vamos a la consola y ejecutamos:

`sudo apt-get update
sudo apt-get install -y git curl zlib1g-dev subversion
sudo apt-get install -y openssl libreadline6-dev git-core zlib1g libssl-dev
sudo apt-get install -y libyaml-dev libsqlite3-dev sqlite3
sudo apt-get install -y libxml2-dev libxslt-dev
sudo apt-get install -y autoconf automake libtool bison`

Para instalar Ruby vamos a utilizar el gestor de versiones [RVM](www.rvm.io "RVM"). Tened en cuenta que esta acción puede tardar un tiempo. El comando para instalar RVM y la última versión estable de ruby es:

`\curl -sSL https://get.rvm.io | bash -s stable --ruby`

Cargamos las variables de RVM en nuestro terminal:

`source ~/.rvm/scripts/rvm`

Y comprobamos que todo ha funcionado ejecutando:

`ruby -v`

Para la instalación de Sinatra y Thin, podemos hacerlo directamente mediante los comandos:

`gem install sinatra`
`gem install thin`

O mediante la gema [Bundler](http://bundler.io/ "Bundler"), para ello instalamos dicha gema:

`gem install bundler`

Y dentro de la carpeta del servidor ejecutamos:

`bundler install`

### Cliente

Como cliente, la aplicación también dispondrá de interfaz web, por lo que cualquier dispositivo podra acceder a ella. Para la aplicación móvil basta con tener un dispositivo Android con una versión igual o superior a la 2.3

## Camina hijo...

Para iniciar el servidor, basta con situarnos con la consola en la carpeta de Server y ejecutar:

`ruby app.rb`

Esto iniciará el servidor en el puerto 61456.