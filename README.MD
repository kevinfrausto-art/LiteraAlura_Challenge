# **Literalura**

Literalura es una aplicación que permite gestionar y explorar libros y autores utilizando datos obtenidos de la API de [Gutendex](https://gutendex.com/). Proporciona funcionalidades para buscar libros, registrar autores, listar registros existentes y consultar libros según diferentes criterios.

## **Características**

- **Buscar libros por título**: Consulta libros en la API de Gutendex y registra automáticamente los datos en la base de datos.
- **Listar libros registrados**: Visualiza todos los libros almacenados en la base de datos.
- **Listar autores registrados**: Detalla los autores guardados junto con sus libros asociados.
- **Filtrar autores por año**: Consulta autores vivos en un año específico.
- **Listar libros por idioma**: Filtra libros por idiomas como español, inglés, francés o portugués.

---

## **Requisitos previos**

Antes de ejecutar la aplicación, asegúrate de tener lo siguiente instalado:

- **Java 17** o superior
- **Maven 3.8.1** o superior
- **PostgreSQL 12** o superior

---

## **Instalación**

### **1. Clonar el repositorio**
```bash
git clone <URL_DEL_REPOSITORIO>
cd literalura


2. Configurar las variables de entorno
Configura las siguientes variables de entorno para conectar la aplicación a tu base de datos PostgreSQL:

Variable	Descripción	Ejemplo
DB_HOST	Host de la base de datos	localhost
DB_PORT	Puerto de la base de datos	5432
DB_NAME	Nombre de la base de datos	literalura
DB_USERNAME	Usuario de la base de datos	postgres
DB_PASSWORD	Contraseña de la base de datos	tu_contraseña
Alternativamente, edita el archivo src/main/resources/application.properties:

properties
Copiar código
spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=postgres
spring.datasource.password=tu_contraseña

3. Crear la base de datos
Crea una base de datos en PostgreSQL con el siguiente comando:

sql
Copiar código
CREATE DATABASE literalura;
4. Construir el proyecto
Ejecuta el siguiente comando para compilar el proyecto y descargar las dependencias necesarias:

bash
Copiar código
mvn clean install
Uso
Ejecutar la aplicación
Para iniciar la aplicación, ejecuta:

bash
Copiar código
mvn spring-boot:run
Menú principal
Al ejecutar la aplicación, se mostrará el siguiente menú interactivo en la consola:

Copiar código
1 - Buscar libro por título
2 - Lista de libros registrados
3 - Lista de autores registrados
4 - Lista de autores vivos en un determinado tiempo
5 - Lista de libros por idioma
0 - Salir
Tecnologías utilizadas
Java 17
Spring Boot 3.3.5
Spring Data JPA
PostgreSQL (Base de datos relacional)
Gutendex API (Para consulta de datos de libros)
Jackson Databind (Procesamiento de datos JSON)
Estructura del proyecto
plaintext
Copiar código
src
├── main
│   ├── java/com/alura/literalura
│   │   ├── principal/Principal.java       # Clase principal
│   │   ├── repository                     # Repositorios JPA
│   │   ├── service                        # Servicios para consumo de API y transformación de datos
│   │   └── model                          # Clases modelo: Autor, Libro, Datos
│   └── resources
│       ├── application.properties         # Configuración de la aplicación
│       └── static                         # Archivos estáticos
└── test                                   # Pruebas


Contribuir
¡Las contribuciones son bienvenidas! Por favor, sigue estos pasos:

Haz un fork del proyecto.
Crea una rama para tu funcionalidad: git checkout -b feature/nueva-funcionalidad.
Realiza los cambios y haz un commit: git commit -m "Agrega nueva funcionalidad".
Envía tus cambios al repositorio remoto: git push origin feature/nueva-funcionalidad.
Abre un Pull Request.


