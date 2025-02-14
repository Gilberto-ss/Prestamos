package Service;

import ModelBD.UsuariosBD;
import ModelBD.UsuariosBD.Rol;
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
    public Response guardar(@FormParam("nombre_usuario") String nombreUsuario,
                        @FormParam("password") String password,
                        @FormParam("primer_nombre") String primerNombre,
                        @FormParam("segundo_nombre") String segundoNombre,
                        @FormParam("apellido_paterno") String apellidoPaterno,
                        @FormParam("apellido_materno") String apellidoMaterno,
                        @FormParam("correo") String correo,
                        @FormParam("telefono") String telefono,
                        @FormParam("rol") String rol,
                        @FormParam("activo") int activo) {

    // Validaciones básicas
    if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El nombre de usuario es obligatorio.\"}")
                .build();
    }
    if (password == null || password.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"La contraseña es obligatoria.\"}")
                .build();
    }
    if (primerNombre == null || primerNombre.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El primer nombre es obligatorio.\"}")
                .build();
    }
    if (apellidoPaterno == null || apellidoPaterno.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El apellido paterno es obligatorio.\"}")
                .build();
    }
    if (correo == null || correo.trim().isEmpty() ) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El correo es obligatorio y no debe superar los 100 caracteres.\"}")
                .build();
    }
    if (telefono != null && telefono.length() > 15) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El teléfono no debe superar los 15 caracteres.\"}")
                .build();
    }
    if (rol == null || rol.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El rol es obligatorio.\"}")
                .build();
    }
    if (!(rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("ASESOR") || rol.equalsIgnoreCase("USUARIO"))) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Rol no válido. Debe ser ADMIN, ASESOR o USUARIO.\"}")
                .build();
    }
    if (activo <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El campo 'activo' debe ser mayor a 0.\"}")
                .build();
    }

    Session session = null;
    Transaction transaction = null;

    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        UsuariosBD usuario = new UsuariosBD();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setPassword(password);
        usuario.setPrimerNombre(primerNombre);
        usuario.setSegundoNombre(segundoNombre);
        usuario.setApellidoPaterno(apellidoPaterno);
        usuario.setApellidoMaterno(apellidoMaterno);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setRol(Rol.valueOf(rol.toUpperCase())); 
        usuario.setFechaCreacion(new Date());
        usuario.setActivo(activo == 1); 

        session.save(usuario);
        transaction.commit();

        return Response.ok("{\"message\":\"Usuario guardado exitosamente.\"}").build();

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

