/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package Service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import modelBD.UsuariosBD;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * REST Web Service
 *
 * @author Gilberto
 */
@Path("Usuarios")
public class UsuariosResource {

    private static SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @POST
    @Path("guardar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardar(@FormParam("nombre_usuario") String nombre_usuario,
            @FormParam("contrase�a") String contrase�a na,
                        @FormParam("rol")String rol,
                        @FormParam("correo") String correo,
                        @FormParam("activo") int activo
        ) {

    if (nombre_usuario == null || nombre_usuario.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"nombre_usuario es obligatorio.\"}")
                    .build();
        }
        if (contrase�a {
            
        }na == null || contrase�a
        na.trim().isEmpty()
        
            ) {
        return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"contrase�a es obligatoria.\"}")
                    .build();
        }
        if (rol == null || rol.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"rol es obligatorio.\"}")
                    .build();
        }
        if (correo == null || correo.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"correo es obligatorio.\"}")
                    .build();
        }
        if (activo == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"activo es obligatorio y debe ser mayor a 0.\"}")
                    .build();
        }

        Session session = null;
        Transaction transaction = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaCreacionParsed;
            fechaCreacionParsed = new Date();

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            UsuariosBD usuario = new UsuariosBD();
            usuario.setNombre_usuario(nombre_usuario);
            usuario.setContrase�a
            na(contrase�a
            );
        usuario.setRol(rol);
            usuario.setCorreo(correo);
            usuario.setFecha_creacion(fechaCreacionParsed);
            usuario.setActivo(activo == 1);

            session.save(usuario);
            transaction.commit();

            return Response.ok("{\"message\":\"Usuario guardado exitosamente\"}")
                    .build();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            // Imprime la traza completa para obtener detalles
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se pudo guardar el usuario: " + e.getClass().getName() + " - " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @GET
    @Path("eliminar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminar(@QueryParam("id") Long id) {
        Session session = null;
        Transaction transaction = null;

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            UsuariosBD usuario = session.get(UsuariosBD.class, id);

            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado con id: " + id + "\"}")
                        .build();
            }

            usuario.setActivo(false);
            session.update(usuario);
            transaction.commit();

            return Response.ok("{\"message\":\"Usuario desactivado exitosamente\"}")
                    .build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al desactivar el usuario: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @POST
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editar(@FormParam("id") long id,
            @FormParam("nombre_usuario") String nombre_usuario,
            @FormParam("contrase�a") String contrase�a,
                       @FormParam("rol")
    String rol,
                       @FormParam("correo") String correo,
                       @FormParam("fecha_creacion") String fecha_creacion,
                       @FormParam("activo") int activo

    
        ) {
    
    Session session = null;
        Transaction transaction = null;

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaCreacionParsed = fecha_creacion != null ? dateFormat.parse(fecha_creacion) : null;

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            UsuariosBD usuario = session.get(UsuariosBD.class, id);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado.\"}")
                        .build();
            }

            if (nombre_usuario != null && !nombre_usuario.trim().isEmpty()) {
                usuario.setNombre_usuario(nombre_usuario);
            }
            if (contrase�a {
                
            }na != null && !contrase�a
            na.trim().isEmpty()
            ) {
            usuario.setContrase�a
                na(contrase�a
            
            );
        }
        if (rol != null) {
                usuario.setRol(rol);
            }
            if (correo != null && !correo.trim().isEmpty()) {
                usuario.setCorreo(correo);
            }
            if (fechaCreacionParsed != null) {
                usuario.setFecha_creacion(fechaCreacionParsed);
            }
            usuario.setActivo(activo == 1 ? true : false);

            session.update(usuario);
            transaction.commit();

            return Response.ok("{\"message\":\"Usuario actualizado exitosamente\"}")
                    .build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al actualizar el usuario: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
