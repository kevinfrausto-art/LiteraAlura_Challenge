package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutoresRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private static final String URL_BASE = "https://gutendex.com/books/?search=";
    private AutoresRepository repositorioAutor;
    private LibroRepository repositorioLibro;

    public Principal(AutoresRepository repositorioAutor, LibroRepository repositorioLibro) {
        this.repositorioAutor = repositorioAutor;
        this.repositorioLibro = repositorioLibro;
    }

    public void muestraElMenu() {
        var opt = -1;
        System.out.println("Hola, selecciona una opción en el menú:");
        while (opt != 0) {
            var menu = """
                    1 - Buscar libro por título
                    2 - Lista de libros registrados
                    3 - Lista de autores registrados
                    4 - Lista de autores vivos en un determinado tiempo 
                    5 - Lista de libros por idioma 
                    0 - salir
                    """;
            System.out.println(menu);

            if (!teclado.hasNextInt()) {
                System.out.println("¡Ingresa una opción válida!");
                teclado.nextLine();  // Limpiar el buffer
                continue;
            }

            opt = teclado.nextInt();
            teclado.nextLine();  // Limpiar el buffer
            switch (opt) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorFecha();
                    break;
                case 5:
                    listarLibrosPorIdiomas();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("¡Opción no válida!");
                    break;
            }
        }
    }

//     Método para obtener datos del libro desde la API
    private Datos obtenerDatosLibro(String tituloLibro) {
        try {
            // Codifica el título del libro para la URL
            String tituloLibroCodificado = URLEncoder.encode(tituloLibro, StandardCharsets.UTF_8.toString());

            // Llama a la API con el título codificado
            var json = consumoAPI.obtenerDatos(URL_BASE + tituloLibroCodificado);

            // Verifica si la respuesta es nula o vacía
            if (json == null || json.isEmpty()) {
                System.out.println("Respuesta vacía de la API. No hay datos para procesar.");
                return null;
            }

            // Deserializa el JSON
            Datos datosLibro = conversor.obtenerDatos(json, Datos.class);

            // Verifica si no hay resultados
            if (datosLibro == null || datosLibro.resultados().isEmpty()) {
                System.out.println("No se encontraron resultados para el título: " + tituloLibro);
                return null;
            }

            return datosLibro;
        } catch (Exception e) {
            System.out.println("Error al obtener datos: " + e.getMessage());
            return null;
        }
    }
    private void buscarLibroPorTitulo() {
        System.out.println("Ingresa el nombre del libro:");
        String tituloLibro = teclado.nextLine(); // Recibe el título del libro

        Datos datos = obtenerDatosLibro(tituloLibro); // Obtiene los datos de la API

        if (datos != null) {
            System.out.println("Resultados de la búsqueda:");
            for (DatosLibro datosLibro : datos.resultados()) {
                System.out.println("Título: " + datosLibro.titulo());
                System.out.println("Autor(es): " + datosLibro.autor().stream()
                        .map(autor -> autor.nombreAutor()).toList());
                System.out.println("Lenguaje(s): " + datosLibro.idioma());
                System.out.println("********************************");

                // Verificar si el libro ya existe en la base de datos
                Optional<Libro> libro = repositorioLibro.findByTituloIgnoreCase(datosLibro.titulo().toLowerCase());

                if (libro.isPresent()) {
                    System.out.println("El libro ya está en la base de datos.");
                } else {
                    System.out.println("El libro no se encontró en la base de datos.");

                    // Crear un nuevo libro y agregarlo a la base de datos
                    Libro nuevoLibro = new Libro();
                    nuevoLibro.setTitulo(datosLibro.titulo());
                    nuevoLibro.setIdioma(datosLibro.idioma().toString());

                    // Crear o recuperar el autor del libro
                    for (DatosAutor autorDatos : datosLibro.autor()) {
                        // Verificar si el autor ya existe en la base de datos
                        Autor autor = obtenerAutor(autorDatos);
                        nuevoLibro.setAutor(autor);  // Asociar el autor al libro
                        nuevoLibro.setNombreAutor(autor.getNombreAutor());  // Nombre del autor
                    }

                    // Guardar el libro en la base de datos
                    repositorioLibro.save(nuevoLibro);
                    System.out.println("El libro ha sido guardado en la base de datos.");
                }
            }
        }
    }
    private Autor obtenerAutor(DatosAutor datosAutor) {
        // Verificar si el autor ya está en la base de datos
        Autor autorExistente = repositorioAutor.findBynombreAutorIgnoreCase(datosAutor.nombreAutor());
        if (autorExistente != null) {
            return autorExistente;  // Devolver el autor existente
        } else {
            // Si no existe, crear un nuevo autor
            Autor nuevoAutor = new Autor(datosAutor);
            return repositorioAutor.save(nuevoAutor);  // Guardar el nuevo autor y devolverlo
        }
    }

    private  void  listarLibrosRegistrados(){
        List<Libro>libro =repositorioLibro.findAll();
        if (libro.isEmpty()){
            System.out.println("No hay libros registrados");
            return;
        }
        System.out.println("los libros registrados son : ");
        libro.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
    }
    private void listarAutoresRegistrados() {
        List<Autor> autores = repositorioAutor.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        System.out.println("Los autores registrados son:\n");

        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombreAutor)) // Ordenar por nombre
                .forEach(autor -> {
                    System.out.println("-----------AUTORES----------");
                    System.out.println("Nombre del autor: " + autor.getNombreAutor());

                    // Obtener los libros asociados a este autor
                    List<Libro> libros = repositorioLibro.findByAutor(autor);

                    if (libros.isEmpty()) {
                        System.out.println("Libros registrados: Ninguno");
                    } else {
                        System.out.println("Libros registrados: " +
                                libros.stream()
                                        .map(Libro::getTitulo)
                                        .toList()); // Mapea a los títulos de los libros
                    }
                    System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
                    System.out.println("Fecha de fallecimiento: " +
                            (autor.getFechaFallecimiento() != null
                                    ? autor.getFechaFallecimiento()
                                    : "Aún vive"));

                    System.out.println("--------------------------------\n");
                });
    }
    private void  listarAutoresPorFecha(){
        System.out.println("Escribe el año que  deseas realizar la busqueda: ");
        var fecha = teclado.nextInt();
        teclado.nextLine();
        if (fecha < 0){
            System.out.println("Año invalido, registre otra fecha");
            return;
        }
        List<Autor>autorPorFecha =
                repositorioAutor
                        .findByFechaDeNacimientoLessThanEqualAndFechaFallecimientoGreaterThanEqual
                                (fecha,fecha);
        if (autorPorFecha.isEmpty()){
            System.out.println("¡No hay autores registrados con esa fecha!");
            return;
        }
        System.out.println("Autores vivos  en el año " + fecha + " Son :");
        autorPorFecha.stream()
                .sorted(Comparator.comparing(Autor::getNombreAutor))
                .forEach(System.out::println);
    }

    private  void  listarLibrosPorIdiomas(){
        System.out.println("Selecciona el idioma  que deseas buscar :");
        String menu = """
                es - Epañol
                en - Ingles
                fr - Frances
                pt - Portugues
                """;
        System.out.println(menu);
        var idioma = teclado.nextLine();
        if (!idioma.equals("es") && !idioma.equals("en") && !idioma.equals("fr") && !idioma.equals("pt")) {
            System.out.println("Idioma no válido, intenta de nuevo");
            return;
        }
        List<Libro> libroIdioma = repositorioLibro.findByIdiomaContaining(idioma);
        if (libroIdioma.isEmpty()){
            System.out.println("La base de datos no contiene libro con ese idioma");
            return;
        }
        System.out.println(" Estos son los libros que estan en el idioma que selecionaste: \n");
        libroIdioma.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
    }









}
