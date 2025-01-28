package Service;

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
import modelBD.AsesoresBD;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Path("Asesores")
public class Asesores {

    private static SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @POST
    @Path("guardar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardar(@FormParam("nombre") String nombre,
                            @FormParam("apellido") String apellido,
                            @FormParam("correo") String correo,
                            @FormParam("telefono") int telefono,
                            @FormParam("direccion") String direccion,
                            @FormParam("fecha_contratacion") String fecha_contratacion,
                            @FormParam("activo") int activo) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El nombre es obligatorio.\"}")
                    .build();
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El apellido es obligatorio.\"}")
                    .build();
        }
        if (correo == null || correo.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El correo es obligatorio.\"}")
                    .build();
        }
        if (telefono <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El teléfono es obligatorio.\"}")
                    .build();
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La dirección es obligatoria.\"}")
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaContratacionParsed = dateFormat.parse(fecha_contratacion);

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            AsesoresBD asesor = new AsesoresBD();
            asesor.setNombre(nombre);
            asesor.setApellido(apellido);
            asesor.setCorreo(correo);
            asesor.setTelefono(telefono);
            asesor.setDireccion(direccion);
            asesor.setFecha_contratacion(fechaContratacionParsed);
            asesor.setActivo(activo == 1);

            session.save(asesor);
            transaction.commit();

            return Response.ok("{\"message\":\"Asesor guardado exitosamente\"}").build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se pudo guardar el asesor: " + e.getMessage() + "\"}")
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

            AsesoresBD asesor = session.get(AsesoresBD.class, id);

            if (asesor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Asesor no encontrado.\"}")
                        .build();
            }

            asesor.setActivo(false);
            session.update(asesor);
            transaction.commit();

            return Response.ok("{\"message\":\"Asesor desactivado exitosamente\"}").build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al desactivar el asesor: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
