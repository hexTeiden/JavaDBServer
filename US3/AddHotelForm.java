import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AddHotelForm extends JFrame {

    //Eingabefelder für die Hotel Stammdaten
    JTextField categoryField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField ownerField = new JTextField();
    JTextField contactField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField cityField = new JTextField();
    JTextField cityCodeField = new JTextField();
    JTextField roomsField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField bedsField = new JTextField();
    JTextField tagsField = new JTextField();

    //Button zum speichern
    JButton saveButton = new JButton("Save Hotel");

    private final HttpClient client = HttpClient.newHttpClient();

    public AddHotelForm() {
        HttpResponse<String> response = null;

        try{

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/api/hotels"))
                    .GET()
                    .build();

            response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        }
        catch(IOException | InterruptedException e){
            e.printStackTrace();
        }

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
        String serverResponse = response.body();

        int newId = 0;
        for (int i = 0; i < serverResponse.length(); i++) {
            if (serverResponse.charAt(i) == '}') newId++;
        }
        int[] hotelId = {newId};

        mainPanel.add(new JLabel("Hotel ID"));
        mainPanel.add(new JLabel(String.valueOf(hotelId[0])));

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

        mainPanel.add(new JLabel("Number Rooms"));
        mainPanel.add(roomsField);

        mainPanel.add(new JLabel("Number Beds"));
        mainPanel.add(bedsField);

        mainPanel.add(new JLabel("Phone"));
        mainPanel.add(phoneField);

        mainPanel.add(new JLabel("Tags"));
        mainPanel.add(tagsField);

        //Platzhalter und Speicherbutton
        mainPanel.add(new JLabel(""));
        mainPanel.add(saveButton);

        //Panel zum ausfüllen mittig
        this.add(mainPanel, BorderLayout.CENTER);

        //Speichervorgang beim Button - Hotel wird erst beim Click erstellt
        saveButton.addActionListener(e -> {
            try {
                Hotel hotel = new Hotel(
                        hotelId[0],
                        Integer.parseInt(roomsField.getText()),
                        Integer.parseInt(bedsField.getText()),
                        categoryField.getText(),
                        nameField.getText(),
                        ownerField.getText(),
                        contactField.getText(),
                        addressField.getText(),
                        cityField.getText(),
                        cityCodeField.getText(),
                        phoneField.getText(),
                        tagsField.getText()
                );
                postHotel(hotel);
                hotelId[0]++;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Rooms und Beds müssen Zahlen sein.");
            } catch (IOException | InterruptedException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        //Fenster zentrieren und sichtbar machen
        setLocationRelativeTo(null);
        setVisible(true);

    }
    private void postHotel(Hotel hotel) throws IOException, InterruptedException {
        String json = "{"
                + "\"id\":" + hotel.id() + ","
                + "\"noRooms\":" + hotel.noRooms() + ","
                + "\"noBeds\":" + hotel.noBeds() + ","
                + "\"category\":\"" + hotel.category() + "\","
                + "\"name\":\"" + hotel.name() + "\","
                + "\"owner\":\"" + hotel.owner() + "\","
                + "\"contact\":\"" + hotel.contact() + "\","
                + "\"address\":\"" + hotel.address() + "\","
                + "\"city\":\"" + hotel.city() + "\","
                + "\"cityCode\":\"" + hotel.cityCode() + "\","
                + "\"phone\":\"" + hotel.phone() + "\","
                + "\"tags\":\"" + hotel.tags() + "\""
                + "}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/api/hotels"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST Status: " + postResponse.statusCode());
        System.out.println("POST Body: " + postResponse.body());
    }
}
