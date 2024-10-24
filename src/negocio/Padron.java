package negocio;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class Padron{
	private ArrayList<Habitante> listaHabitantes = new ArrayList<>();

	public Padron(){
		cargarHabitantes();
	}

	private void cargarHabitantes(){
		Scanner sc = null;
		try{
			File fichero = new File("padron.csv");
			//Crea el fichero si no existe
			fichero.createNewFile();
			sc = new Scanner(fichero);
			sc.useDelimiter(",|\n");
			while(sc.hasNext()){
				listaHabitantes.add(new Habitante(sc.next(), sc.next(), sc.next()));
			}
		}catch(IOException ex){
			System.out.println("Error en la lectura del fichero de habitantes.");
			System.out.println("A continuación se muestra más información:");
			System.out.println(ex);
		}finally{
			if (sc != null) sc.close();
		}

	}

	public void annadir(Habitante habitante){
		listaHabitantes.add(habitante);
		volcarHabitantes();
	}

	private void volcarHabitantes(){
		FileWriter fw = null;
		try{
			fw = new FileWriter("padron.csv");
			for(Habitante habitante : listaHabitantes){
				fw.write(habitante.getNombre() + "," + habitante.getApellido1() + "," + habitante.getApellido2()+"\n");
			}
		}catch(IOException ex){
			System.out.println("No se ha podido añadir el nuevo habitante. Error en la escritura del fichero");
			System.out.println("A continuación se muestra más información:");
			System.out.println(ex);
		}finally{
			try{
				if (fw != null) fw.close();
			}catch(IOException ex){
				System.out.println(ex);
			}
		}
	}

	@Override
        public String toString(){
		StringBuilder strHabitantes = new StringBuilder();
		for(Habitante habitante : listaHabitantes) strHabitantes.append(habitante + "\n"); 
		return strHabitantes.toString();
	}	
}
