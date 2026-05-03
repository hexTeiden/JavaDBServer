import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class EditTransactionalData extends JFrame {
    //window --> JFrame
    String url = "jdbc:sqlserver://185.119.119.126:1433;databaseName=DanubeDevs;encrypt=true;trustServerCertificate=true";
    String user = "danube";
    String password = "danube";
    int newId = 0;

    //attribute der klasse = Fields
    private final JComboBox<String> combobox = new JComboBox<>();
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField roomField = new JTextField();
    private final JTextField bedField = new JTextField();
    private final JTextField yearField = new JTextField();
    private final JTextField monthField = new JTextField();
    private final JTextField usedRoomsField = new JTextField();
    private final JTextField usedBedField = new JTextField();
    private final JButton saveButton = new JButton("Save");


    public EditTransactionalData(Hotel hotel) throws SQLException {
        //Screen dem Hotelobjekt übergeben wird
        setTitle("Edit Transaction Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null); // in der Mitte vom Screen

        setupUI(hotel);//Methodenaufruf und übergabe

        getHotelsData();

        setupListeners();
    }

    //Layout vom Fenster
    private void setupUI(Hotel hotel) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); //Händelt Absatzabstand
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));// macht rahmen abstand nicht pickenden text)

        //werte einfügen
        idField.setText(String.valueOf(hotel.id));
        nameField.setText(hotel.name);
        roomField.setText(String.valueOf(hotel.noRooms));
        bedField.setText(String.valueOf(hotel.noBeds));

        //unveränderbare Felder:
        idField.setEditable(false);
        nameField.setEditable(false);
        roomField.setEditable(false);
        bedField.setEditable(false);

        //fenster aufgebaut und die felder hinzugefügt
        JPanel inputPanel = new JPanel(new GridLayout(12, 2, 10, 10));//eine row fürs bild
        inputPanel.add(new JLabel("Name"));
        inputPanel.add(combobox);
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Hotelname:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Number of rooms:"));
        inputPanel.add(roomField);
        inputPanel.add(new JLabel("Number of beds:"));
        inputPanel.add(bedField);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Month(in Number f.eg.: 1):"));
        inputPanel.add(monthField);
        inputPanel.add(new JLabel("Used Rooms:"));
        inputPanel.add(usedRoomsField);
        inputPanel.add(new JLabel("Used Beds:"));
        inputPanel.add(usedBedField);

        saveButton.setEnabled(true);

        mainPanel.add(new LogoComponent(),  BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    //zuerst zuhören dann login
    private void setupListeners() {
        // Validation listener ensures that fields aren't empty/too short
        saveButton.addActionListener(e -> validateInputs());// wenn eingabe passt, button und weiter zur handle safe methode
        combobox.addActionListener(e -> {
            try {
                getHotelId();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void getHotelsData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password)){
            String query = "select * from hotels";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                combobox.addItem(rs.getString("name"));
            }
        }
    }

    public void getHotelId() throws SQLException {
        String selectedName = (String) combobox.getSelectedItem();
        if (selectedName == null) return;

        try (Connection conn = DriverManager.getConnection(url, user, password)){
            String query = "select * from hotels where name = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, selectedName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                newId = rs.getInt("id");
                System.out.println(newId);
                idField.setText(String.valueOf(newId));
                nameField.setText(selectedName);
                roomField.setText(String.valueOf(rs.getInt("noRooms")));
                bedField.setText(String.valueOf(rs.getInt("noBeds")));
            }
        }
    }

    private void handleSave() throws SQLException {
        //TODO: save occupancy to Database
        try (Connection conn = DriverManager.getConnection(url, user, password)){
            String sql = """
                    insert into occupancies(hotel_id, rooms, usedRooms, beds, usedBeds, year, month)
                    values(?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setInt(1 ,newId);
                ps.setInt(2, Integer.parseInt(roomField.getText()));
                ps.setInt(3, Integer.parseInt(usedRoomsField.getText()));
                ps.setInt(4, Integer.parseInt(bedField.getText()));
                ps.setInt(5, Integer.parseInt(usedBedField.getText()));
                ps.setInt(6, Integer.parseInt(yearField.getText()));
                ps.setInt(7, Integer.parseInt(monthField.getText()));
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
    }

    //login window: es können nicht mehr betten belegt werden, als vorhanden. string in integer umwandeln zum vergleich
    private void validateInputs() {//weil in der klasse, nicht nötig zu +bergeben
        try {
            if (Integer.parseInt(usedRoomsField.getText()) > Integer.parseInt(roomField.getText())) {
                JOptionPane.showMessageDialog(this, "The number of usedRooms has to be lower than the existing one's.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(usedBedField.getText()) > Integer.parseInt(bedField.getText())) {
                JOptionPane.showMessageDialog(this, "The number of usedBeds has to be lower than the existing one's.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(yearField.getText()) < 1970) {
                JOptionPane.showMessageDialog(this, "Year has to be at least 1970", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(monthField.getText())<1||Integer.parseInt(monthField.getText())>12) {
                JOptionPane.showMessageDialog(this, "This month does not exist.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                handleSave();
            }
        } catch (NumberFormatException e) {//wenn parseInt nciht zahlen sieht wird exception geworfen nämlich numberformatexception
            JOptionPane.showMessageDialog(this, "Please enter a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
