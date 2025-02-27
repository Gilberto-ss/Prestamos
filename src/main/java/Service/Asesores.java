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
import ModelBD.AsesoresBD;
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
public Response guardar(@FormParam("primer_nombre") String primer_nombre,
                        @FormParam("segundo_nombre") String segundo_nombre,
                        @FormParam("apellido_paterno") String apellido_paterno,
                        @FormParam("apellido_materno") String apellido_materno,                       
                        @FormParam("telefono") int telefono,
                        @FormParam("correo") String correo,
                        @FormParam("activo") int activo) {

    if (primer_nombre == null || primer_nombre.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El primer nombre es obligatorio.\"}")
                .build();
    }
    if (apellido_paterno == null || apellido_paterno.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El apellido paterno es obligatorio.\"}")
                .build();
    }
    if (apellido_materno == null || apellido_materno.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El apellido materno es obligatorio.\"}")
                .build();
    }
    if (telefono <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El teléfono debe ser un número válido mayor a 0.\"}")
                .build();
    }
    if (correo == null || correo.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El correo es obligatorio.\"}")
                .build();
    }
    if (activo != 0 && activo != 1) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El campo 'activo' debe ser 0 o 1.\"}")
                .build();
    }

    try (Session session = sessionFactory.openSession()) {
        Transaction transaction = session.beginTransaction();

        AsesoresBD asesor = new AsesoresBD();
        asesor.setPrimer_nombre(primer_nombre);
        asesor.setSegundo_nombre(segundo_nombre);
        asesor.setApellido_paterno(apellido_paterno);
        asesor.setApellido_materno(apellido_materno);
        asesor.setTelefono(telefono);
        asesor.setCorreo(correo);
        asesor.setActivo(activo == 1);

        session.save(asesor);
        transaction.commit();

        return Response.ok("{\"message\":\"Asesor guardado exitosamente\"}").build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"No se pudo guardar el asesor: " + e.getMessage() + "\"}")
                .build();
    }
}

}
