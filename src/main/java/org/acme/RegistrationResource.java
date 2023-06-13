package org.acme;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/app")
public class RegistrationResource {
    private static List<User> users = new ArrayList<>();


    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(UserRegistrationRequest request) {
        if (!isUsernameAvailable(request.getUsername())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El nombre de usuario ya está en uso.")
                    .build();
        }

        if (!arePasswordsMatching(request.getPassword(), request.getConfirmPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Las contraseñas no coinciden.")
                    .build();
        }

        User user = new User(request.getUsername(), request.getPassword());
        users.add(user);

        return Response.ok("Registro exitoso").build();
    }

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(UserLoginRequest request) {

        User user = getUserByUsername(request.getUsername());

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("usuario no existe")
                    .build();
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("contraseña no valida")
                    .build();
        }

        String message = "Usuario eliminado: " + user.getUsername();
        users.remove(user);

        return Response.ok(message).build();
    }
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(UserLoginRequest request) {
        User user = getUserByUsername(request.getUsername());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas.")
                    .build();
        }

        UserLoginResponse response = new UserLoginResponse(user.getUsername());
        return Response.ok(response).build();
    }

    @POST
    @Path("/restore")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response RestablecerContraseña(UserLoginRequest request) {
        if (isUsernameAvailable(request.getUsername()) || request.getUsername() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El usuario no existe.").build();
        }
        User user = getUserByUsername(request.getUsername());
        if(request.getPassword()==user.getPassword() || request.getPassword() == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La contraseña no debe ser la misma.").build();
        }
        user.setPassword(request.getPassword());
        return Response.ok(user.getUsername()).build();
    }

    private User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public static class UserLoginRequest {
        private String username;
        private String password;

        // Agrega getters y setters para los campos

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class UserLoginResponse {
        private String user;
        private String userStatus;

        public UserLoginResponse(String user) {
            this.user = user;
            this.userStatus = "loggedIn";
        }

        // Agrega getters y setters para los campos

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }
    }

    private boolean isUsernameAvailable(String username) {
        return users.stream()
                .noneMatch(user -> user.getUsername().equals(username));
    }

    private boolean arePasswordsMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static class UserRegistrationRequest {
        private String username;
        private String password;
        private String confirmPassword;

        // Agrega getters y setters para los campos

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    public static class User {
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Agrega getters y setters para los campos

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
