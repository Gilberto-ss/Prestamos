package Service;

import ModelBD.UsuariosBD;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

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
                            @FormParam("password") String password,
                            @FormParam("rol") String rol,
                            @FormParam("correo") String correo,
                            @FormParam("activo") int activo) {
        
        if (nombre_usuario == null || nombre_usuario.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"nombre_usuario es obligatorio.\"}")
                    .build();
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"password es obligatoria.\"}")
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
        if (activo != 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'activo' debe ser 1.\"}")
                    .build();
        }

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            UsuariosBD usuario = new UsuariosBD();
            usuario.setNombre_usuario(nombre_usuario);
            usuario.setpassword(password);
            usuario.setRol(rol);
            usuario.setCorreo(correo);
            usuario.setFecha_creacion(new Date());
            usuario.setActivo(true);

            session.save(usuario);
            transaction.commit();

            return Response.ok("{\"message\":\"Usuario guardado exitosamente\"}").build();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al guardar el usuario: " + e.getMessage() + "\"}")
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
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }

        Session session = null;
        Transaction transaction = null;
        
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

            return Response.ok("{\"message\":\"Usuario desactivado exitosamente\"}").build();

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
}

