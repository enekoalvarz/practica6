package pck;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

@SuppressWarnings("serial")
public class NuevaVentana extends JFrame{

	private int elementosBorrados = 1;
	protected int poblacionMunicipioSeleccionado = -1;
	protected JTable tabla;
	protected int poblacionCelda;
	private boolean ordenar;
	private String provSelec;

	//COLUMNAS POR NOMBRES

	private int COL_CODIGO = 0;
	private int COL_NOMBRE = 1;
	private int COL_HABITANTES = 2;
	private int COL_PROVINCIA = 3;
	private int COL_AUTONOMIA = 4;
	private int COL_INGRESOS = 5;
	private int COL_PARO = 6;
	private int COL_POBLACION = 7;
	

	public NuevaVentana(DataSetMunicipios dataset, VentanaTablaDatos ventanaTablaDatos) {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 1000, 600 );
		setLocationRelativeTo( null );
		setVisible(true);

		JPanel main = new JPanel(new BorderLayout());
		add(main);
		
		//Texto NORTH
		JPanel textoArriba = new JPanel(); //en JPanel para que vaya centrado
		textoArriba.add(new JLabel("LOS MUNICIPIOS DE ESPAÑA SI"));
		main.add(textoArriba, BorderLayout.NORTH);
		
		
		
		//Tabla, CENTER
		tabla = new JTable();

		JScrollPane tablaScrollPane = new JScrollPane(tabla);
		main.add(tablaScrollPane, BorderLayout.CENTER);
		
		mostrarTablaCompleta(ventanaTablaDatos, dataset, tabla); //hago que la tabla del medio sea la original del principio
		
		
		//Arbol WEST
		DefaultTreeModel modeloTree = dataset.getModeloTree();
		JTree arbol = new JTree(modeloTree);
		JProgressBar progressBar = new JProgressBar(0,10);
		arbol.setCellRenderer(new DefaultTreeCellRenderer(){
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				Object userObject = node.getUserObject();

				if (userObject != null && !node.isLeaf() && !node.isRoot()) {
					progressBar.setValue(node.getLeafCount());
					progressBar.setStringPainted(true);
					progressBar.setPreferredSize(new Dimension(70, progressBar.getPreferredSize().height-3));
					JPanel panel = new JPanel(new BorderLayout());
					panel.add(c, BorderLayout.WEST);
					panel.add(progressBar, BorderLayout.EAST);
					return panel;
				}
				return c;
				}
			});

		JScrollPane arbolSrollPane = new JScrollPane(arbol);
		main.add(arbolSrollPane, BorderLayout.WEST);
		
		arbol.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
				if(selectedNode != null && selectedNode.isLeaf()) {
					provSelec = selectedNode.toString();
					cargarMunicipiosconProvincia(provSelec, dataset, tabla);
				}

				/*
				if(selectedNode.toString().equals("Municipios")) {
					mostrarTablaCompleta(ventanaTablaDatos, dataset, tabla);
				}
				 */
				
			}
			
		});
	
		
		//Panel, EAST
		JPanel panel = new JPanel();
		//panel.setBackground(Color.green);
		main.add(panel, BorderLayout.EAST);
		
		//Botonera, SOUTH
		JPanel botonera = new JPanel();
		JButton insertar = new JButton("Insertar");
		JButton borrar = new JButton("Borrar");
		JButton orden = new JButton("Orden");
		botonera.add(insertar); botonera.add(borrar); botonera.add(orden);
		main.add(botonera, BorderLayout.SOUTH);



		insertar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() { //Uso un SwingWorker para que no interrumpa la ejecucion y se haga en segundo plano, sino da error ConcurrentModificationException.
					@Override
					protected Void doInBackground() {
						int codigo = dataset.getListaMunicipios().getLast().getCodigo()+1;
						String nombre= "";
						int habitantes = 500000;
						String provincia = (String) tabla.getValueAt(tabla.getSelectedRow(), COL_PROVINCIA);
						String autonomia = (String) tabla.getValueAt(tabla.getSelectedRow(), COL_AUTONOMIA);
						int ingresos = 0;
						double paro = 0;

						Municipio nuevo = new Municipio(codigo, nombre, habitantes, provincia, autonomia, ingresos, paro);
						dataset.getListaMunicipios().add(nuevo);
						System.out.println(dataset.getListaMunicipios());
						return null;
					}

					@Override
					protected void done() {
						if(provSelec == null){
							//AQUI RECARGAR TABLA GENERAL - NO ME SALE - FALTA
						}else{
							cargarMunicipiosconProvincia(provSelec, dataset, tabla);
						}
					}
				};

				worker.execute(); // Inicia el SwingWorker
			}
		});

		borrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() {
						int index = -1;
						for(Municipio muni : dataset.getListaMunicipios()){
							index++;
							if(muni.getCodigo() == (int)tabla.getValueAt(tabla.getSelectedRow(), COL_CODIGO)){
								dataset.getListaMunicipios().remove(index);

							}
					}
					return null;
					}

					@Override
					protected void done() {
						if(provSelec == null){
							//RECARGAR TABLA GENERAL - NO HAY MANERA - FALTA
						}else{
							cargarMunicipiosconProvincia(provSelec, dataset, tabla);
						}
					}
				};
				worker.execute(); // inicio el SwingWorker


			}
		});


		orden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(provSelec == null){
					JOptionPane.showMessageDialog(null, "Este boton solo se puede usar para la tabla de provincias");
				}else{
					ordenar = !ordenar;
					cargarMunicipiosconProvincia(provSelec, dataset, tabla);
				}

			}
		});

		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickEnColumna = tabla.columnAtPoint(e.getPoint());
				if (e.getButton() == MouseEvent.BUTTON3 && clickEnColumna==1){
					if(poblacionMunicipioSeleccionado ==-1){
						int clickEnFila = tabla.rowAtPoint(e.getPoint());
						poblacionMunicipioSeleccionado = (int) tabla.getValueAt(clickEnFila,COL_HABITANTES);
					}else{
						poblacionMunicipioSeleccionado = -1;
					}
				}
			}
		});


		//RENDERER PARA LA TABLA INICIAL CON TODO MEZCLADO
		tabla.setDefaultRenderer( String.class, new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				c.setBackground(Color.WHITE);
				if(column == COL_NOMBRE) {
					poblacionCelda = (int) tabla.getValueAt(row, COL_HABITANTES);
					if(poblacionMunicipioSeleccionado == -1 || poblacionCelda==poblacionMunicipioSeleccionado){
						c.setBackground(Color.WHITE);
					}else if(poblacionCelda > poblacionMunicipioSeleccionado){
						c.setBackground(Color.RED);
					}else if(poblacionCelda < poblacionMunicipioSeleccionado){
						c.setBackground(Color.green);
					}
				}

				DefaultCellEditor noEditable = new DefaultCellEditor(new JTextField());
				noEditable.setClickCountToStart(Integer.MAX_VALUE); // Practicamente imposible editar las celdas. Sé que no es asi pero no encuentro manera para esta tabla.

				if(column==COL_PROVINCIA || column==COL_AUTONOMIA){
					tabla.getColumnModel().getColumn(COL_PROVINCIA).setCellEditor(noEditable);
					tabla.getColumnModel().getColumn(COL_AUTONOMIA).setCellEditor(noEditable);
				}

				return c;
			}
		});
	
	}


	private void cargarMunicipiosconProvincia(String provinciaSelec, DataSetMunicipios dataset, JTable tabla) {
	    ArrayList<Municipio> muniEnProvincia = new ArrayList<>();

	    for (Municipio muni : dataset.getListaMunicipios()) {
	        if (muni.getProvincia().equals(provinciaSelec)) {
	            muniEnProvincia.add(muni);
	        }
	    }

		//depende el boton de ordenar se va a hacer de una manera u otra
	    if(ordenar == false){
			muniEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
		}else{
			muniEnProvincia.sort(Comparator.comparing(Municipio::getHabitantes));
		}


	    DefaultTableModel model2 = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != COL_AUTONOMIA && column != COL_PROVINCIA;
			}
		};

		model2.addColumn("Código");
	    model2.addColumn("Nombre");
	    model2.addColumn("Habitantes");
	    model2.addColumn("Provincia");
	    model2.addColumn("Autonomía");
		model2.addColumn("Ingresos Medios");
		model2.addColumn("Tasa de Paro");
		model2.addColumn("Poblacion"); //aqui progressbar (num 6)


		for (Municipio muni : muniEnProvincia) {
			int codigo = muni.getCodigo();
	        String nombre = muni.getNombre();
	        int habitantes = muni.getHabitantes();
	        String provincia = muni.getProvincia();
	        String autonomia = muni.getAutonomia();
			int ingresos = muni.getIngresos();
			double paro = muni.getParo();

	        model2.addRow(new Object[]{codigo,nombre, habitantes, provincia, autonomia, ingresos, paro, habitantes});
	    }

		//RENDERER DE LA TABLA DE PROVINCIAS
		tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (column == COL_POBLACION) {
					int habitantes = (Integer) value;
					JProgressBar progressBar = new JProgressBar(50000, 5000000);
					progressBar.setValue(habitantes);

					//cambiar fondo
					float green = 1 - (float) habitantes / 5000000; // Rango de 0 (verde) a 1 (rojo)
					float red = (float) habitantes / 5000000; // Rango de 1 (verde) a 0 (rojo)
					progressBar.setForeground(new Color(red, green, 0));

					return progressBar;
				}

				if(column == COL_NOMBRE) {
					int poblacionCelda = (int) tabla.getValueAt(row, COL_HABITANTES);
					if(poblacionMunicipioSeleccionado == -1 || poblacionCelda==poblacionMunicipioSeleccionado){
						c.setBackground(Color.WHITE);
					}else if(poblacionCelda > poblacionMunicipioSeleccionado){
						System.out.println(poblacionCelda);
						c.setBackground(Color.RED);
					}else if(poblacionCelda < poblacionMunicipioSeleccionado){
						c.setBackground(Color.green);
					}
				}


				return c;
			}


		});

	    tabla.setModel(model2);
	    
		
	}

	private void mostrarTablaCompleta(VentanaTablaDatos ventanaTablaDatos, DataSetMunicipios dataset, JTable tabla){
		ventanaTablaDatos.setDatos(dataset, tabla);
		tabla.setBackground(Color.white);
	}
}
