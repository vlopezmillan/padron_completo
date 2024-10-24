package presentacion;

import negocio.*;

public class InterfazUsuario{
	private static String negrita = "\u001B[1m";
	private static String normal = "\033[0m";
	private static String rojo = "\u001B[31m";
	private static String blanco = "\u001B[37m";
	public static void ejecutar(String[] instruccion){
// Si escribo `java -jar padron.jar añadir Juan García Martínez`
// entonces
// `instruccion[0]` es `añadir`
// `instruccion[1]` es `Juan`
// `instruccion[2]` es `García`
// `instruccion[3]` es `Martínez`
		Padron padron = new Padron();
	
		if (instruccion.length == 0 || instruccion[0].equalsIgnoreCase("ayuda") && instruccion.length == 1){
			ayuda();
		}else if (instruccion[0].equalsIgnoreCase("mostrar") && instruccion.length == 1){
			mostrarHabitantes(padron);
		}else if (instruccion[0].equalsIgnoreCase("añadir") && instruccion.length == 4){
			Habitante habitante = new Habitante(instruccion[1], instruccion[2], instruccion[3]);
			padron.annadir(habitante);
		}else{
			System.out.println(rojo + "El formato utilizado en la entrada es incorrecto" + blanco);
			ayuda();
		}
	}

	private static void mostrarHabitantes(Padron padron){
		System.out.println(padron);
	}

	private static void ayuda(){
		System.out.println("\n"+negrita+"DESCRIPCIÓN"+normal);
		System.out.println("\tEsta aplicación ofrece las siguientes funcionalidades:\n\n" + 
				"\t- Permite añadir un nuevo habitante al padrón\n" + 
				"\t- Permite mostrar los habitantes del padrón");
		System.out.println(negrita+"FORMATO"+normal);
		System.out.println("\tPara añadir un nuevo habitante, se escribe:\n");
		System.out.println("\t\t" + negrita+ "java -jar padron.jar añadir <nombre> <apellido1> <apellido2>" + normal +"\n");
		System.out.println("\tPara mostrar los habitantes del padrón, se teclea:\n");
		System.out.println("\t\t" + negrita + "java -jar padron.jar mostrar" + normal + "\n");
		System.out.println("\tPara mostrar esta ayuda, se escribe:\n");
		System.out.println("\t\t" + negrita +"java -jar padron.jar ayuda" + normal + "\n");
		System.out.println(negrita+"EJEMPLOS"+normal);
		System.out.println("\tEjemplo 1\n");
		System.out.println("\t\t" + negrita +"java -jar padron.jar añadir Juan García Martínez" + normal + "\n");
		System.out.println("\tEjemplo 2\n");
		System.out.println("\t\t" + negrita + "java -jar padron.jar mostrar" + normal+ "\n");
		System.out.println("\tEjemplo 3\n");
		System.out.println("\t\t" + negrita + "java -jar padron.jar ayuda" + normal + "\n");
	}

}
