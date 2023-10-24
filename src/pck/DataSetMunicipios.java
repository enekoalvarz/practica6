package pck;

import java.io.*;
import java.util.*;

import javax.swing.tree.*;

/** Permite gestionar datasets de municipios. Cada objeto contiene un dataset de 'n' municipios
 */
public class DataSetMunicipios extends DatasetParaJTable {
	private DefaultTreeModel modeloTree;
	
	/** Crea un nuevo dataset de municipios, cargando los datos desde el fichero indicado
	 * @param nombreFichero	Nombre de fichero o recurso en formato de texto. En cada línea debe incluir los datos de un municipio <br>
	 * separados por tabulador: código nombre habitantes provincia autonomía
	 * @throws IOException	Si hay error en la lectura del fichero
	 */
	public DataSetMunicipios( String nombreFichero ) throws IOException {
		super( new Municipio( 0, "", 0, "", "", 0, 0.0 ) );
		File ficMunicipios = new File( nombreFichero );
		Scanner scanner = null;
		if (ficMunicipios.exists()) {
			scanner = new Scanner( ficMunicipios );
		} else {
			scanner = new Scanner( DataSetMunicipios.class.getResourceAsStream( nombreFichero ) );
		}
		int numLinea = 0;
		
		//INICIALIZAR EL ARBOL
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Municipios");
		modeloTree = new DefaultTreeModel(root);
		
		
        Map<String, DefaultMutableTreeNode> mapaCA = new HashMap<>();
        Map<String, DefaultMutableTreeNode> mapaProv = new HashMap<>();
        ArrayList<String> comunidades= new ArrayList<>();
        ArrayList<String> provincias = new ArrayList<>();
        ArrayList<String> municipios = new ArrayList<>();
        
		while (scanner.hasNextLine()) {
			numLinea++;
			String linea = scanner.nextLine();
			String[] partes = linea.split( "\t" );
			try {
				int codigo = Integer.parseInt( partes[0] );
				String nombre = partes[1];
				int habitantes = Integer.parseInt( partes[2] );
				String provincia = partes[3];
				String comunidad = partes[4];
				int ingresos = Integer.parseInt( partes[5] );
				double paro = Double.parseDouble( partes[6] );
				Municipio muni = new Municipio( codigo, nombre, habitantes, provincia, comunidad, ingresos, paro );
				add( muni );
				
				//---------TREE-----------
				DefaultMutableTreeNode comunidadAutonomaNode = new DefaultMutableTreeNode(comunidad);
				DefaultMutableTreeNode provinciaNode = new DefaultMutableTreeNode(provincia);
				DefaultMutableTreeNode municipioNode = new DefaultMutableTreeNode(nombre);
				
				//comunidades autonomas
				if(!comunidades.contains(comunidad)) {
					root.add(comunidadAutonomaNode);
					
				}
				comunidades.add(comunidad);
				
				//Provincias 
				if(!mapaCA.containsKey(comunidad)) {
					mapaCA.put(comunidad, comunidadAutonomaNode);
				}else {
					comunidadAutonomaNode = mapaCA.get(comunidad);	
				}
				
				if(!provincias.contains(provincia)) {
					comunidadAutonomaNode.add(provinciaNode);
					provincias.add(provincia);
				}
				
				/*
				//Municipios
				if(!mapaProv.containsKey(provincia)) {
					mapaProv.put(provincia, provinciaNode);
				}else {
					provinciaNode = mapaProv.get(provincia);	
				}
				
				if(!municipios.contains(nombre)) {
					provinciaNode.add(municipioNode);
					municipios.add(nombre);
				}
				*/
				
				
			        
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				System.err.println( "Error en lectura de línea " + numLinea );
				System.out.println(e.getMessage());
			}
		}
		
		
	}

	
	//para poder pasarlo a la ventana
	public DefaultTreeModel getModeloTree() {
		return modeloTree;
	}
	
	/** Devuelve la lista de municipios
	 * @return	Lista de municipios
	 */
	@SuppressWarnings("unchecked")
	public List<Municipio> getListaMunicipios() {
		return (List<Municipio>) getLista();
	}
	
	/** Añade un municipio al final
	 * @param muni	Municipio a añadir
	 */
	public void anyadir( Municipio muni ) {
		add( muni );
	}
	
	/** Añade un municipio en un punto dado
	 * @param muni	Municipio a añadir
	 * @param posicion	Posición relativa del municipio a añadir (de 0 a n)
	 */
	public void anyadir( Municipio muni, int posicion ) {
		anyadeFila( posicion, muni );
	}
	
	/** Quita un municipio
	 * @param codigoMuni	Código del municipio a eliminar
	 */
	public void quitar( int codigoMuni ) {
		for (int i=0; i<size(); i++) {
			if (((Municipio)get(i)).getCodigo() == codigoMuni) {
				borraFila( i );
				return;
			}
		}
	}

	// Queremos que las celdas sean editables excepto el código
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		}
		return true;
	}
	
}
