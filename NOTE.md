# Proyecto Quarkus

Hi! I'm your first Markdown file in **StackEdit**. If you want to learn about StackEdit, you can read me. If you want to play with Markdown, you can edit me. Once you have finished with me, you can create new files by opening the **file explorer** on the left corner of the navigation bar.


# 1. Introducción
Hoy en día, es muy común escribir una aplicación y desplegarla en la nube y despreocuparse de la infraestructura. Serverless y FaaS se han vuelto muy populares.

En este tipo de entornos, donde las instancias se crean y destruyen con frecuencia, el tiempo de arranque y el tiempo a la primera petición son extremadamente importantes, ya que pueden crear una experiencia de usuario completamente diferente.

Lenguajes como JavaScript y Python están siempre en el punto de mira en este tipo de escenarios. En otras palabras, Java, con sus JAR gordos y su largo tiempo de arranque, nunca fue uno de los principales contendientes.

En este tutorial, presentaremos Quarkus.

# 2. Quarkus
QuarkusIO, promete ofrecer artefactos pequeños, un tiempo de arranque extremadamente rápido y un menor tiempo hasta la primera petición. Cuando se combina con GraalVM, Quarkus compila antes de tiempo (AOT).

Y, puesto que Quarkus está construido sobre estándares, no necesitamos aprender nada nuevo. En consecuencia, podemos utilizar CDI y JAX-RS, entre otros. Además, Quarkus tiene un montón de extensiones, incluyendo las que soportan Hibernate, Kafka, OpenShift, Kubernetes, y Vert.x.

# 3. Nuestra primera aplicación
La forma más sencilla de crear un nuevo proyecto Quarkus es abrir un terminal y escribir:

````
mvn io.quarkus:quarkus-maven-plugin:2.16.4.Final:create \
    -DprojectGroupId=com.demo.quarkus \
    -DprojectArtifactId=quarkus-project \
    -DclassName="com.demo.quarkus.HelloResource" \
    -Dpath="/demo"
````

Esto generará el esqueleto del proyecto, un HelloResource con un /hello endpoint expuesto, configuración, proyecto Maven, y Dockerfiles.

Una vez importados a nuestro IDE, tendremos una estructura similar a la que se muestra en la imagen de abajo:
![enter image description here](https://www.baeldung.com/wp-content/uploads/2019/05/quarkus-project.png)

Examinemos el contenido de la clase HelloResource:

````
package com.demo.quarkus;  
  
import javax.inject.Inject;  
import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  
import javax.ws.rs.core.MediaType;  
  
@Path("/hola")  
public class HolaResource {  
  
    @Inject  
  HolaService holaService;  
  
    @GET  
 @Produces(MediaType.TEXT_PLAIN)  
    public String hola() {  
        return "Hola";  
    }  
  
    @GET  
 @Produces(MediaType.APPLICATION_JSON)  
    @Path("v1/polite/{name}")  
    public String hola2(@PathParam("name") String name) {  
        return helloService.politeHello(name);  
    }  
  
    @GET  
 @Produces(MediaType.APPLICATION_JSON)  
    @Path("v2/polite/{name}")  
    public String hola3(@PathParam("name") String name) {  
        return helloService.politeHello2(name);  
    }  
}
````

Todo parece ir bien hasta ahora. En este punto, tenemos una aplicación sencilla con un único endpoint JAX-RS de RESTEasy. Vamos a seguir adelante y probarlo abriendo un terminal y ejecutar el comando:

````
./mvnw compile quarkus:dev:
````

![enter image description here](https://www.baeldung.com/wp-content/uploads/2019/05/mvn_compile_quarkus_dev.png)

Nuestro punto final REST debería estar expuesto en localhost:8080/hello. Vamos a probarlo con el comando curl:

````
$ curl localhost:8080/hola
hola
````
# 4. Recarga en caliente (Hot Reload)

Cuando se ejecuta en modo de desarrollo (./mvn compile quarkus:dev), Quarkus proporciona una capacidad de recarga en caliente. En otras palabras, **los cambios realizados en los archivos Java o en los archivos de configuración se compilarán automáticamente una vez que se actualice el navegador**. La característica más impresionante aquí es que no necesitamos guardar nuestros archivos. Esto puede ser bueno o malo, dependiendo de nuestras preferencias.

Ahora modificaremos nuestro ejemplo para demostrar la capacidad de recarga en caliente. Si la aplicación se detiene, podemos simplemente reiniciarla en modo dev. Usaremos el mismo ejemplo de antes como punto de partida.

Primero, crearemos una clase HolaService:
````
package com.demo.quarkus;  
  
import org.eclipse.microprofile.config.inject.ConfigProperty;  
  
import javax.enterprise.context.ApplicationScoped;  
  
@ApplicationScoped  
public class HolaService {  
  
    @ConfigProperty(name = "buenosdias")  
    String saludos;  
  
    public String politeHello(String name){  
        return "Hola " + name;  
    }  
  
    public String politeHello2(String name){  
        return "Hola " + name + " " + saludos;  
    }  
}
````

Ahora, modificaremos la clase HelloResource, inyectando el HelloService y añadiendo un nuevo método:

````
@Inject
HolaService holaService;

@GET  
@Produces(MediaType.APPLICATION_JSON)  
@Path("v1/polite/{name}")  
public String hola2(@PathParam("name") String name) {  
    return helloService.politeHello(name);  
}
````

A continuación, vamos a probar nuestro nuevo endpoint:
````
$ curl localhost:8080/hola/v1/polite/Baeldung
Hello Mr/Mrs Inetum
````

Haremos un cambio más para demostrar que lo mismo puede aplicarse a los archivos de propiedades. Vamos a editar el archivo application.properties y añadir una clave más:

````
quarkus.http.port=8082  
  
buenosdias=Buenos dias  
buenastardes=Buenas tardes  
buenasnoches=Buenas noches
````

Después, modificaremos el HelloService para que utilice nuestra nueva propiedad:

````
@GET  
@Produces(MediaType.APPLICATION_JSON)  
@Path("v2/polite/{name}")  
public String hola3(@PathParam("name") String name) {  
    return helloService.politeHello2(name);  
}
````

Podemos empaquetar fácilmente la aplicación ejecutando:

````
./mvnw package
````

````
java -jar target/quarkus-project-1.0-SNAPSHOT-runner.jar
````

# 5. Imagen nativa

A continuación, produciremos una imagen nativa de nuestra aplicación. Una imagen nativa mejorará el tiempo de arranque y el tiempo hasta la primera respuesta. En otras palabras, **contiene todo lo que necesita para ejecutarse, incluyendo la JVM mínima necesaria para ejecutar la aplicación**.

Para empezar, necesitamos tener GraalVM instalado y la variable de entorno GRAALVM_HOME configurada.

Ahora pararemos la aplicación (Ctrl + C), si no está parada ya, y ejecutaremos el comando:

````
./mvnw package -Pnative
````

Esto puede tardar unos segundos en completarse. Debido a que las imágenes nativas intentan crear todo el código AOT para arrancar más rápido, como resultado, tendremos tiempos de compilación más largos.

Podemos ejecutar ./mvnw verify -Pnative para verificar que nuestro artefacto nativo fue construido correctamente:
![enter image description here](https://www.baeldung.com/wp-content/uploads/2019/05/native-verify.png)

En segundo lugar, **crearemos una imagen de contenedor utilizando nuestro ejecutable nativo**. Para ello, debemos tener un runtime de contenedor (es decir, Docker) ejecutándose en nuestra máquina. Vamos a abrir una ventana de terminal y ejecutar:

````
./mvnw package -Pnative -Dnative-image.docker-build=true
````

Esto creará un ejecutable Linux de 64 bits, por lo tanto, si estamos usando un sistema operativo diferente, puede que ya no sea ejecutable. Eso está bien por ahora.

La generación del proyecto creó un Dockerfile.native para nosotros:

````
FROM registry.fedoraproject.org/fedora-minimal
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
````

Si examinamos el archivo, tenemos una pista de lo que viene a continuación. En primer lugar, vamos a **crear una imagen docker**:

````
docker build -f src/main/docker/Dockerfile.native -t quarkus/quarkus-project .
````
Ahora, podemos ejecutar el contenedor utilizando

````
docker run -i --rm -p 8080:8080 quarkus/quarkus-project
````

![enter image description here](https://www.baeldung.com/wp-content/uploads/2019/05/docker-native.png)

El contenedor arrancó en un tiempo increíblemente bajo de 0,009s. Ese es uno de los puntos fuertes de Quarkus.

Por último, debemos probar nuestro REST modificado para validar nuestra aplicación:

````
$ curl localhost:8080/hola/v1/polite/Inetum
````

# 6. Desplegar en OpenShift

Una vez que hayamos terminado de probar localmente usando Docker, desplegaremos nuestro contenedor en OpenShift. Suponiendo que tenemos la imagen de Docker en nuestro registro, podemos desplegar la aplicación siguiendo los siguientes pasos:

````
oc new-build --binary --name=quarkus-project -l app=quarkus-project
oc patch bc/quarkus-project -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.native"}}}}'
oc start-build quarkus-project --from-dir=. --follow
oc new-app --image-stream=quarkus-project:latest
oc expose service quarkus-project
````

Ahora, podemos obtener la URL de la aplicación ejecutando:

````
oc get route
````

Por último, accederemos al mismo endpoint (ten en cuenta que la URL puede ser diferente, dependiendo de nuestra dirección IP):

````
$ curl http://quarkus-project-myproject.192.168.64.2.nip.io/hola/v1/polite/Baeldung
Hola Inetum
````

# 7. Conclusión


En este artículo, demostramos que Quarkus es una gran adición que puede llevar Java de manera más eficaz a la nube. Por ejemplo, ahora es posible imaginar Java en AWS Lambda. Además, Quarkus se basa en estándares como JPA y JAX/RS. Por lo tanto, no necesitamos aprender nada nuevo.