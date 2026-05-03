import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddHotelForm extends JFrame {
    // Datenbank connection url, user und password
    String url = "jdbc:sqlserver://185.119.119.126:1433;databaseName=DanubeDevs;encrypt=true;trustServerCertificate=true";
    String user = "danube";
    String password = "danube";
    int newId = 0;

    //Eingabefelder für die Hotel Stammdaten
    JTextField categoryField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField ownerField = new JTextField();
    JTextField contactField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField cityField = new JTextField();
    JTextField cityCodeField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField roomsField = new JTextField();
    JTextField bedsField = new JTextField();
    JTextField tagsField = new JTextField();
    //Button zum speichern
    JButton saveButton = new JButton("Save Hotel");

    public AddHotelForm() throws SQLException {
        getHotelsData();

        //Frame Einstellungen
        setTitle("Add New Hotel");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Haupt-Layout: BorderLayout erlaubt die Trennung in Logo (Nord) und Formular (Mitte)
        setLayout(new BorderLayout());

        //Logo jetzt mal auskommentiert damit keine Fehler sind
        //add(new LogoComponet(), BorderLayout.NORTH);

        //Panel für die Formularfelder mit GridLayout (Label links, Textfeld rechts)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(13, 2,10, 10));

        //Gestaltung
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,10,10,10), BorderFactory.createTitledBorder("Hotel Details")));

        //Label zeigt an, dass ID automatisch und nicht editierbar
        mainPanel.add(new JLabel("Hotel ID"));
        mainPanel.add(new JLabel(String.valueOf(newId)));

        //Stammdatenfelden hinzufügen
        mainPanel.add(new JLabel("Category"));
        mainPanel.add(categoryField);

        mainPanel.add(new JLabel("Name"));
        mainPanel.add(nameField);

        mainPanel.add(new JLabel("Owner"));
        mainPanel.add(ownerField);

        mainPanel.add(new JLabel("Contact"));
        mainPanel.add(contactField);

        mainPanel.add(new JLabel("Address"));
        mainPanel.add(addressField);

        mainPanel.add(new JLabel("City"));
        mainPanel.add(cityField);

        mainPanel.add(new JLabel("City Code"));
        mainPanel.add(cityCodeField);

        mainPanel.add(new JLabel("Phone"));
        mainPanel.add(phoneField);

        mainPanel.add(new JLabel("Number Rooms"));
        mainPanel.add(roomsField);

        mainPanel.add(new JLabel("Number Beds"));
        mainPanel.add(bedsField);

        mainPanel.add(new JLabel("Tags"));
        mainPanel.add(tagsField);

        //Platzhalter und Speicherbutton
        mainPanel.add(new JLabel(""));
        mainPanel.add(saveButton);

        //Panel zum ausfüllen mittig
        this.add(mainPanel, BorderLayout.CENTER);

        //Speichervorgang beim Button
        saveButton.addActionListener(e -> saveHotel());

        //Fenster zentrieren und sichtbar machen
        setLocationRelativeTo(null);
        setVisible(true);
    }
    //Sammelt Daten aus den Feldern und gibt es zur Speicher-Methode
    private void saveHotel() {
       try (Connection conn = DriverManager.getConnection(url, user, password)){
           String query = """
                   insert into hotels(id, noRooms, noBeds, category, name, owner, contact, address, city, cityCode, phone, tags)
                   values(?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,?)
                   """;
           try (PreparedStatement ps = conn.prepareStatement(query)){
               ps.setInt(1, newId);
               ps.setInt(2, Integer.parseInt(roomsField.getText()));
               ps.setInt(3, Integer.parseInt(bedsField.getText()));
               ps.setString(4, categoryField.getText());
               ps.setString(5, nameField.getText());
               ps.setString(6, ownerField.getText());
               ps.setString(7, contactField.getText());
               ps.setString(8, addressField.getText());
               ps.setString(9, cityField.getText());
               ps.setString(10, cityCodeField.getText());
               ps.setString(11, phoneField.getText());
               ps.setString(12, tagsField.getText());

               ps.executeUpdate();
           }
        }
       catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }

    public void getHotelsData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password)){
            String query = "select * from hotels";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                newId = rs.getInt(1) + 1;
            }
        }
    }
}
