package controllers;

import controllers.ControllerHelper;
import play.mvc.Controller;
import models.Ruta;
import models.MyUser;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class RutaController extends Controller
{
	
	
	 //Metodo GET /usuario/<uId>/ruta
	//Json y XML aceptados
	public static Result getRuta(Long uId){
		Result res;
		
		MyUser usuario = MyUser.finder.byId(uId);		
		if (usuario == null) {
			return notFound();
		}
		
		if (ControllerHelper.acceptsJson(request())) {
			res = ok(Json.toJson(usuario.getRutas()));
		}
		else if (ControllerHelper.acceptsXml(request())) {
			res = ok(views.xml.rutas.render(usuario.getRutas()));
		}
		else {
			res = badRequest(ControllerHelper.errorJson(1, "unsupported_format", null));
		}
			
		return res; 
	}
	

	//Metodo POST para crear la ruta de un usuario
	// /usuario/<uId>/ruta
	//si el objeto Ruta es insertado incorrectamente devuelve ruta invalida
	//sino la guarda
	public static Result create(Long uId) {
		Form<Ruta> form = Form.form(Ruta.class).bindFromRequest();

		if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_phone", form.errorsAsJson()));
		}
		
		MyUser usuario = MyUser.finder.byId(uId);		
		if (usuario == null) {
			return notFound();
		}

		Ruta url = form.get();
		
		url.setUser(usuario);
		url.save();
		
		return created();
	}

	//Metodo DELETE /usuario/<uId>/ruta/<tId>
	 // Necesario el id del usuario y el id de la ruta
	//primero busca la url sino existe, responde notFound y despues se comprueba 
	//si existe el usuario y se borra la ruta, sino existe usuario la respuesta es usuario invalido
	public static Result delete(Long uId, Long tId) {
		Ruta url = Ruta.finder.byId(tId);
		if (url == null) {
			return notFound();
		}
		
		if (!url.getUser().getId().equals(uId)) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_user", null));
		}

		url.delete();

		return ok();
	}
}
