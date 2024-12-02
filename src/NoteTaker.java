import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//JFrame for functions related to frame, containing the application, ActionListener for user input
public class NoteTaker extends JFrame implements ActionListener {

    // Declaring GUI components
    JTextArea textArea;
    JScrollPane scrollPane; // Adds a scrollable view to the text area
    JSpinner fontSizeSpinner; // Spinner to adjust font size
    JLabel fontLabel; // Label for the font size spinner
    JButton fontColorButton; // Button to change the font color
    JComboBox fontBox; // Dropdown to select font family
    JMenuBar menuBar; // Menu bar for file operations
    JMenu fileMenu; // Menu for file operations
    JMenuItem openItem; // Menu item to open a file
    JMenuItem saveItem; // Menu item to save a file
    JMenuItem exitItem; // Menu item to exit the application

    // Constructor to initialize the GUI components
    NoteTaker() {
        // Setting up the JFrame properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Note Pad");
        this.setSize(500, 500); // initial size, width and height
        this.setLayout(new FlowLayout()); // meant for layout of components, no values passed so initial values working here
        this.setLocationRelativeTo(null); // Center the window on screen
        this.setVisible(true);

        // Initializing and setting up the text area
        textArea = new JTextArea();
        textArea.setLineWrap(true); // Enable line wrapping
        textArea.setWrapStyleWord(true); // Wrap words instead of breaking them, so word moves to next line if insufficient space present
        textArea.setFont(new Font("Arial", Font.PLAIN, 20)); // Default font

        // Adding a scroll pane for the text area
        scrollPane = new JScrollPane(textArea); //scrollPane for textArea
        scrollPane.setPreferredSize(new Dimension(450, 450)); // Scroll pane dimensions, setPreferredSize so child component aka textArea, also gets dimension of 450x450
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Label for font size spinner
        fontLabel = new JLabel("Font: ");

        // Spinner to adjust font size dynamically
        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setValue(20); // Default font size
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Change the font size when spinner value changes
                //Font(fontFamily,fontStyle,fontSize)
                textArea.setFont(new Font(textArea.getFont().getFamily(), Font.PLAIN, (int) fontSizeSpinner.getValue()));
            }
        });

        // Button to change font color using a color chooser dialog
        fontColorButton = new JButton("Color"); //create button labelled as "Color"
        fontColorButton.addActionListener(this);  //actionListener event for when this particular button is pressed

        // Dropdown to select font family
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontBox = new JComboBox(fonts); //dropdown consisting of elements in fonts[]
        fontBox.addActionListener(this); //actionListener for when dropdown is clicked
        fontBox.setSelectedItem("Arial"); // Default font family

        // Creating the menu bar and file menu
        menuBar = new JMenuBar(); //horizontal menu bar, usually contains commands like edit,home,file etc
        fileMenu = new JMenu("File"); //creates a vertical dropdown menu named "File"
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        // Adding action listeners for all menu items
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        // Adding menu items to the file menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu); // Adding the file menu to the menu bar

        // Finally Adding all the created components to the JFrame
        this.setJMenuBar(menuBar);
        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(fontColorButton);
        this.add(fontBox);
        this.add(scrollPane);
    }

    // Handling actions performed by the user
    @Override
    public void actionPerformed(ActionEvent e) {
        // Change font color when the color button is clicked
        if (e.getSource() == fontColorButton) { //if case for when user clicks fontColor button
            JColorChooser colorChooser = new JColorChooser();
            Color color = colorChooser.showDialog(null, "Choose a Color", Color.black); //pop up urging to choose a color
            textArea.setForeground(color); // Set the chosen color as font color
        }

        // Change font family when a font is selected from the dropdown
        if (e.getSource() == fontBox) {
            textArea.setFont(new Font((String) fontBox.getSelectedItem(), Font.PLAIN, textArea.getFont().getSize())); //Font(fontFamily,fontStyle,fontSize)
        }

        // Open a file and display its content in the text area
        if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(".")); // Set default directory, in this case, the project directory

            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) { //if valid file is chosen to be opened
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath()); //new file object with file path as passed value
                Scanner fileIN = null; //declared outside for access to both catch and finally blocks

                try {
                    fileIN = new Scanner(file);
                    if (file.isFile()) { //if selected file is valid
                        //reads the file and basically copies t to textArea
                        while (fileIN.hasNextLine()) {
                            String line = fileIN.nextLine() + "\n";
                            textArea.append(line); // Append each line to the text area
                        }
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace(); //prints stackTrace containing location and reason of error
                } finally {
                    if (fileIN != null) { //closing file scanner if a file is selected at least
                        fileIN.close(); // Close the file scanner
                    }
                }
            }
        }

        // Save the text area content to a file
        if (e.getSource() == saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(".")); // Set default directory

            int response = fileChooser.showSaveDialog(null); //dialog confirming save //null so the pop-up is not attached to any parent

            if (response == JFileChooser.APPROVE_OPTION) {
                //make new file object, copy text in textArea to new file object, save the file object
                File file;
                PrintWriter fileOut = null;

                file = new File(fileChooser.getSelectedFile().getAbsolutePath()); //path of file wanting to be saved
                try {
                    //exporting text content in textArea
                    fileOut = new PrintWriter(file);
                    fileOut.println(textArea.getText()); // Write text area content to file
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } finally {
                    if (fileOut != null) {
                        fileOut.close(); // Close the file writer
                    }
                }
            }
        }

        // Exit the application when the exit menu item is clicked
        if (e.getSource() == exitItem) {
            System.exit(0); // Terminate the program
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        new NoteTaker(); // Create an instance of the NoteTaker class
    }
}
