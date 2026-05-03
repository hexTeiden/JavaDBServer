import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EditTransactionalData extends JFrame {
    //window --> JFrame

    //attribute der klasse = Fields
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField roomField = new JTextField();
    private final JTextField bedField = new JTextField();
    private final JTextField yearField = new JTextField();
    private final JTextField monthField = new JTextField();
    private final JTextField usedRoomsField = new JTextField();
    private final JTextField usedBedField = new JTextField();
    private final JButton saveButton = new JButton("Save");

    public EditTransactionalData(Hotel hotel) {
        //Screen dem Hotelobjekt übergeben wird
        setTitle("Edit Transaction Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null); // in der Mitte vom Screen

        setupUI(hotel);//Methodenaufruf und übergabe

        setupListeners();
    }

    //Layout vom Fenster
    private void setupUI(Hotel hotel) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); //Händelt Absatzabstand
        JComboBox<String> comboBox = new JComboBox<>();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));// macht rahmen abstand nicht pickenden text)

        final HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/api/hotels" ))
                    .GET()
                    .build();

            response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Hotel> hotels = new Gson().fromJson(response.body(), new TypeToken<List<Hotel>>(){}.getType());
        for (Hotel h : hotels) {
            comboBox.addItem(h.name());
        }

        final Hotel[] selectedHotel = {hotel};
        comboBox.addActionListener(e -> {
            selectedHotel[0] = hotels.get(comboBox.getSelectedIndex());
            idField.setText(String.valueOf(selectedHotel[0].id()));
            nameField.setText(selectedHotel[0].name());
            roomField.setText(String.valueOf(selectedHotel[0].noRooms()));
            bedField.setText(String.valueOf(selectedHotel[0].noBeds()));
        });

        //werte einfügen
        idField.setText(String.valueOf(selectedHotel[0].id()));
        nameField.setText(selectedHotel[0].name());
        roomField.setText(String.valueOf(selectedHotel[0].noRooms()));
        bedField.setText(String.valueOf(selectedHotel[0].noBeds()));

        //unveränderbare Felder:
        idField.setEditable(false);
        nameField.setEditable(false);
        roomField.setEditable(false);
        bedField.setEditable(false);

        //fenster aufgebaut und die felder hinzugefügt
        JPanel inputPanel = new JPanel(new GridLayout(12, 2, 10, 10));//eine row fürs bild
        inputPanel.add(new JLabel("Selected Hotel"));
        inputPanel.add(comboBox);
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
        saveButton.addActionListener(e -> {
            try {
                Occupancies occupancy = new Occupancies(
                        Integer.parseInt(idField.getText()),
                        Integer.parseInt(roomField.getText()),
                        Integer.parseInt(usedRoomsField.getText()),
                        Integer.parseInt(bedField.getText()),
                        Integer.parseInt(usedBedField.getText()),
                        Integer.parseInt(yearField.getText()),
                        Integer.parseInt(monthField.getText())
                );
                handleSave(occupancy);
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void handleSave(Occupancies occupancies) throws IOException, InterruptedException{
        validateInputs();// wenn eingabe passt, button und weiter zur handle safe methode

        HttpClient client = HttpClient.newHttpClient();
        String json = new Gson().toJson(occupancies);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/api/occupancies/" + occupancies.id()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST Status: " +  postResponse.statusCode());
        System.out.println("POST Body: " +  postResponse.body());
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
            }
        } catch (NumberFormatException e) {//wenn parseInt nciht zahlen sieht wird exception geworfen nämlich numberformatexception
            JOptionPane.showMessageDialog(this, "Please enter a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
