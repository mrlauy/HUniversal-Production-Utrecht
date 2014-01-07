/**                                     ______  _______   __ _____  _____
 *                  ...++,              | ___ \|  ___\ \ / /|  _  |/  ___|
 *                .+MM9WMMN.M,          | |_/ /| |__  \ V / | | | |\ `--.
 *              .&MMMm..dM# dMMr        |    / |  __| /   \ | | | | `--. \
 *            MMMMMMMMMMMM%.MMMN        | |\ \ | |___/ /^\ \\ \_/ //\__/ /
 *           .MMMMMMM#=`.gNMMMMM.       \_| \_|\____/\/   \/ \___/ \____/
 *             7HMM9`   .MMMMMM#`		
 *                     ...MMMMMF .      
 *         dN.       .jMN, TMMM`.MM     	@file 	MainGUI.java
 *         .MN.      MMMMM;  ?^ ,THM		@brief 	...
 *          dM@      dMMM3  .ga...g,    	@date Created:	2013-12-18
 *       ..MMM#      ,MMr  .MMMMMMMMr   
 *     .dMMMM@`       TMMp   ?TMMMMMN   	@author	Alexander Hustinx
 *   .dMMMMMF           7Y=d9  dMMMMMr    
 *  .MMMMMMF        JMMm.?T!   JMMMMM#		@section LICENSE
 *  MMMMMMM!       .MMMML .MMMMMMMMMM#  	License:	newBSD
 *  MMMMMM@        dMMMMM, ?MMMMMMMMMF    
 *  MMMMMMN,      .MMMMMMF .MMMMMMMM#`    	Copyright � 2013, HU University of Applied Sciences Utrecht. 
 *  JMMMMMMMm.    MMMMMM#!.MMMMMMMMM'.		All rights reserved.
 *   WMMMMMMMMNNN,.TMMM@ .MMMMMMMM#`.M  
 *    JMMMMMMMMMMMN,?MD  TYYYYYYY= dM     
 *                                        
 *	Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *	- Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *   ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 *   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *   GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *   HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *   OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package simulation.gui;

import java.io.File;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import simulation.BatchSpawner;
import simulation.ProductSpawner;
import simulation.Simulation;
import simulation.collectors.EquipletDataCollector;
import simulation.collectors.ProductDataCollector;
import simulation.mas_entities.Grid;

public class MainGUI extends javax.swing.JFrame {

	// Files
	public static File capabilitiesFile		= new File("C:/Users/Alexander/Documents/School/Jaar 3/Stage/HUniversal/src/REXOS/MAS/simulation/csvFiles/capabilities.csv");
	public static File productsFile			= new File("C:/Users/Alexander/Documents/School/Jaar 3/Stage/HUniversal/src/REXOS/MAS/simulation/csvFiles/productA.csv");
	public static File batchesFile			= new File("C:/Users/Alexander/Documents/School/Jaar 3/Stage/HUniversal/src/REXOS/MAS/simulation/csvFiles/BatchA.csv");
	public static File gridFile				= new File("C:/Users/Alexander/Documents/School/Jaar 3/Stage/HUniversal/src/REXOS/MAS/simulation/csvFiles/equipletLayout.json");

	// BackgroundWorkers
	private ProgressWorkerThread progressWorker;
	private FileCheckerThread fileChecker;

	//Frames
	private EquipletListFrame elf;
	
	// is the Simulator running
	private boolean isRunning 					= false;

	// BackgroundWorker checks
	private boolean isCheckingFiles				= false;
	private boolean isChartWindowOpen			= false;
	private boolean isVisualisationWindowOpen 	= false;
	private boolean isEquipletListOpen 			= false;
	
	private Simulation simulation;
	private Grid grid;

	/**
	 * Creates new form MainGUI
	 */
	public MainGUI() {	
		super("SimGUI");
		initComponents();

		System.out.println("[DEBUG]\t\tCreated Simulator");
		startFileChecker();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")                        
	private void initComponents() {

		startButton = new javax.swing.JButton();
		progressBar = new javax.swing.JProgressBar();
		jSeparator2 = new javax.swing.JSeparator();
		timeLeftLabel = new javax.swing.JLabel();
		progressLabel = new javax.swing.JLabel();
		timeLeftText = new javax.swing.JTextField();
		gridLabel = new javax.swing.JLabel();
		capabilitiesLabel = new javax.swing.JLabel();
		productsLabel = new javax.swing.JLabel();
		batchesLabel = new javax.swing.JLabel();
		gridText = new javax.swing.JTextField();
		capabilitiesText = new javax.swing.JTextField();
		productsText = new javax.swing.JTextField();
		batchesText = new javax.swing.JTextField();
		jMenuBar1 = new javax.swing.JMenuBar();
		menuFile = new javax.swing.JMenu();
		menuFileVisual = new javax.swing.JMenuItem();
		menuFileGraph = new javax.swing.JMenuItem();
		menuFileEquiplet = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JPopupMenu.Separator();
		menuFileExit = new javax.swing.JMenuItem();
		menuEdit = new javax.swing.JMenu();
		menuEditConfig = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		startButton.setText("Start Simulation");
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});

		timeLeftLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		timeLeftLabel.setText("Est. time left:");

		progressLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		progressLabel.setText("Progress:");

		timeLeftText.setText("HH:MM:SS");

		gridLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		gridLabel.setText("Grid:");

		capabilitiesLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		capabilitiesLabel.setText("Capabilities:");

		productsLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		productsLabel.setText("Products:");

		batchesLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		batchesLabel.setText("Batches: ");

		gridText.setText("NOT FOUND");

		capabilitiesText.setText("NOT FOUND");

		productsText.setText("NOT FOUND");

		batchesText.setText("NOT FOUND");

		menuFile.setText("File");

		menuFileVisual.setText("Show Visual");
		menuFileVisual.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuFileVisualActionPerformed(evt);
			}
		});
		menuFile.add(menuFileVisual);

		menuFileGraph.setText("Show Graphs");
		menuFileGraph.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuFileGraphActionPerformed(evt);
			}
		});
		menuFile.add(menuFileGraph);
		
		menuFileEquiplet.setText("Show Equiplet Info");
		menuFileEquiplet.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuFileEquipletActionPerformed(evt);
			}
		});
		menuFile.add(menuFileEquiplet);

		menuFile.add(jSeparator1);

		menuFileExit.setText("Exit");
		menuFileExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuFileExitActionPerformed(evt);
			}
		});
		menuFile.add(menuFileExit);

		jMenuBar1.add(menuFile);

		menuEdit.setText("Edit");

		menuEditConfig.setText("Edit Configurations");
		menuEdit.add(menuEditConfig);

		jMenuBar1.add(menuEdit);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGap(180, 180, 180)
						.addComponent(startButton))
						.addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
								.addGap(100, 100, 100)
								.addComponent(timeLeftLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(timeLeftText, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup()
										.addGap(100, 100, 100)
										.addComponent(progressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(layout.createSequentialGroup()
												.addGap(70, 70, 70)
												.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout.createSequentialGroup()
														.addGap(150, 150, 150)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
																.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
																		.addComponent(gridLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(10, 10, 10)
																		.addComponent(gridText))
																		.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
																				.addComponent(batchesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addGap(10, 10, 10)
																				.addComponent(batchesText))
																				.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
																						.addComponent(productsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addGap(10, 10, 10)
																						.addComponent(productsText))
																						.addGroup(layout.createSequentialGroup()
																								.addComponent(capabilitiesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
																								.addGap(10, 10, 10)
																								.addComponent(capabilitiesText)))) //, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGap(20, 20, 20)
						.addComponent(startButton)
						.addGap(17, 17, 17)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(capabilitiesLabel)
								.addComponent(capabilitiesText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(productsLabel)
										.addComponent(productsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(batchesLabel)
												.addComponent(batchesText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(gridLabel)
														.addComponent(gridText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGap(10, 10, 10)
														.addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(timeLeftLabel)
																.addComponent(timeLeftText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
																.addComponent(progressLabel)
																.addGap(5, 5, 5)
																.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
																.addContainerGap(24, Short.MAX_VALUE))
				);

		setVisible(true);
		pack();
	}// </editor-fold>                        

	private void menuFileEquipletActionPerformed(java.awt.event.ActionEvent evt) {                                           
		if(!isEquipletListOpen || !elf.isActive()){
			// TODO open Equiplet List stuff:
			isEquipletListOpen = true;
			elf = new EquipletListFrame(grid);
		} else {
			System.out.println("[DEBUG]\t\tEquiplet List is already opened ...");
		}
	}             

	private void menuFileGraphActionPerformed(java.awt.event.ActionEvent evt){
		if(!isChartWindowOpen){
			// TODO open Graph Window:
		} else {
			System.out.println("[DEBUG]\t\tChart Window is already opened ...");
		}
	}
	
	private void menuFileVisualActionPerformed(java.awt.event.ActionEvent evt) {                                           
		if(!isVisualisationWindowOpen){
			// TODO open Visualisation Window stuff:
			isVisualisationWindowOpen = true;
		} else {
			System.out.println("[DEBUG]\t\tVisualisation Window is already opened ...");
		}
	}  

	private void menuFileExitActionPerformed(java.awt.event.ActionEvent evt) {          
		shutdownSimulator();   
	} 

	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
		// TODO add your handling code here:
		if(!isRunning){
			if(checkFiles()){
				pauseFileChecker();
				
				try{
					System.out.println("[DEBUG]\t\tStarting Simulation ...");
					startNewSimulation();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				isRunning = true;
				startButton.setText("Stop Simulation");

				progressWorker = new ProgressWorkerThread(this);
				progressWorker.execute();

				// @ TODO Implement Run ...
				//	Simulation.start();

			} else {
				System.out.println("[DEBUG]\t\tNot all Files were Found! \n[DEBUG]\t\tCanceling Simulation ...");
				resumeFileChecker();
				return;
			}
		} else {
			stopSimulation();
		}
	}             

	private void startNewSimulation() throws ParseException {
		simulation = new Simulation();
		EquipletDataCollector edc = new EquipletDataCollector(simulation);
		ProductDataCollector pdc = new ProductDataCollector(simulation);
		
		grid = new Grid("/home/t/sim/equipletLayout.json", simulation);
		simulation.grid = grid;
		System.out.println(grid);
		
		simulation.addUpdateable(grid);
		ProductSpawner ps1 = new ProductSpawner(simulation, grid);
		simulation.addUpdateable(ps1);
		BatchSpawner bs1 = new BatchSpawner(simulation, grid);
		simulation.addUpdateable(bs1);
		
		simulation.resumeSimulation();
	}

	public boolean checkFiles(){
		boolean areAllFilesFound = true;

		if(!capabilitiesFile.exists()){
			capabilitiesText.setText("NOT FOUND");
			areAllFilesFound = false;
		} else							capabilitiesText.setText("FOUND");
		if(!productsFile.exists()){
			productsText.setText("NOT FOUND");
			areAllFilesFound = false;
		} else							productsText.setText("FOUND");
		if(!batchesFile.exists()){
			batchesText.setText("NOT FOUND");
			areAllFilesFound = false;
		} else							batchesText.setText("FOUND");
		if(!gridFile.exists()){
			gridText.setText("NOT FOUND");
			areAllFilesFound = false;
		} else							gridText.setText("FOUND");

		return areAllFilesFound;
	}

	private void startFileChecker(){
		isCheckingFiles = true;
		fileChecker = new FileCheckerThread(this);
		fileChecker.execute();
	}

	private void pauseFileChecker(){
		if(!fileChecker.isPaused()){
			isCheckingFiles = false;
			fileChecker.pause();
		}
	}

	private void resumeFileChecker(){
		if(fileChecker.isPaused()){
			isCheckingFiles = true;
			fileChecker.resume();
		}
	}

	private void stopSimulation(){
		System.out.println("[DEBUG]\t\tStopping Simulation ...");
		simulation.abort();
		simulation = null;
		
		isRunning = false;
		startButton.setText("Start Simulation");
		
		progressWorker.cancel(false);
		resumeFileChecker();
		
	}
	
	private void shutdownSimulator(){
		if(isVisualisationWindowOpen)	isVisualisationWindowOpen = false;
		//if(isVisualisationWindowOpen)	vf.shutdownGUI();
		if(isEquipletListOpen)			elf.shutdownGUI();
		if(isRunning)					stopSimulation();
		
		fileChecker.cancel(false);

		System.out.println("[DEBUG]\t\tShutting down Simulator ...");
		this.dispose();
		//System.exit(0);
	}

	/**
	 * getProgressComponents returns an array with JComponents
	 * @return components[0] = JProgressBar, components[1] = JTextField
	 */
	public JComponent[]	getProgressComponents(){
		JComponent[] components = {progressBar, timeLeftText};
		return components;
	}

	/**
	 * setProgressComponents updates JComponents related to progress
	 * @param components[0] = JProgressBar, components[1] = JTextField
	 */
	public void setProgressComponents(JComponent[] components){
		if(components.length != 2){
			System.out.println("[ERROR]\t\tYou need to send exactly 2 JComponents!");
			return;
		}
		if(components[0] instanceof JProgressBar 
				&& components[1] instanceof JTextField){
			progressBar 	= (JProgressBar) components[0];
			timeLeftText 	= (JTextField) components[1];
		} else if(components[1] instanceof JProgressBar 
				&& components[0] instanceof JTextField){
			progressBar 	= (JProgressBar) components[1];
			timeLeftText 	= (JTextField) components[0];
		} else {
			System.out.println("[ERROR]\t\tYou need to send 1 JProgressBar AND 1 JTextField!");
		}
	}

	public File getCapabilitiesFile() {
		return capabilitiesFile;
	}

	public void setCapabilitiesFile(File capabilitiesFile) {
		if(capabilitiesFile.exists())	this.capabilitiesFile = capabilitiesFile;
		else							System.out.println("[DEBUG]\t\tGiven batchesFile doesn't exist! ...");
	}

	public File getProductsFile() {
		return productsFile;
	}

	public void setProductsFile(File productsFile) {
		if(productsFile.exists())	this.productsFile = productsFile;
		else						System.out.println("[DEBUG]\t\tGiven batchesFile doesn't exist! ...");
	}

	public File getBatchesFile() {
		return batchesFile;
	}

	public void setBatchesFile(File batchesFile) {
		if(batchesFile.exists())	this.batchesFile = batchesFile;
		else						System.out.println("[DEBUG]\t\tGiven batchesFile doesn't exist! ...");
	}

	public File getGridFile() {
		return gridFile;
	}

	public void setGridFile(File gridFile) {
		if(gridFile.exists())		this.gridFile = gridFile;
		else						System.out.println("[DEBUG]\t\tGiven batchesFile doesn't exist! ...");

	}

	public boolean isVisualisationWindowOpen() {
		return isVisualisationWindowOpen;
	}

	public void setVisualisationWindowOpen(boolean isVisualisationWindowOpen) {
		this.isVisualisationWindowOpen = isVisualisationWindowOpen;
	}

	public boolean isEquipletListOpen() {
		return isEquipletListOpen;
	}

	public void setEquipletListOpen(boolean isEquipletListOpen) {
		this.isEquipletListOpen = isEquipletListOpen;
	}


	// Variables declaration - do not modify                     
	private javax.swing.JLabel batchesLabel;
	private javax.swing.JTextField batchesText;
	private javax.swing.JLabel capabilitiesLabel;
	private javax.swing.JTextField capabilitiesText;
	private javax.swing.JLabel gridLabel;
	private javax.swing.JTextField gridText;
	private javax.swing.JMenu menuFile;
	private javax.swing.JMenu menuEdit;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JMenuItem menuFileVisual;
	private javax.swing.JMenuItem menuFileEquiplet;
	private javax.swing.JMenuItem menuFileGraph;
	private javax.swing.JMenuItem menuFileExit;
	private javax.swing.JMenuItem menuEditConfig;
	private javax.swing.JPopupMenu.Separator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JLabel productsLabel;
	private javax.swing.JTextField productsText;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JLabel progressLabel;
	private javax.swing.JButton startButton;
	private javax.swing.JLabel timeLeftLabel;
	private javax.swing.JTextField timeLeftText;
	// End of variables declaration                   

	public Simulation getSimulation() {
		return simulation;
	}
}
