package Service;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Gilberto
 */
@javax.ws.rs.ApplicationPath("api") // Puedes cambiar "api" si necesitas otra ruta
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * No modificar el método addRestResourceClasses().
     * Se llena automáticamente con todos los recursos REST en el proyecto.
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(Service.Asesores.class);
        resources.add(Service.Clientes.class);
        resources.add(Service.CorsFilter.class);
        resources.add(Service.Login.class);
        resources.add(Service.Prestamos.class);
        resources.add(Service.UsuariosResource.class);

    }
}
