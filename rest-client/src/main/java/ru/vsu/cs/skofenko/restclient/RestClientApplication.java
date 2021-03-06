package ru.vsu.cs.skofenko.restclient;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.vsu.cs.skofenko.restclient.visualization.MainForm;

import java.util.Locale;

@SpringBootApplication
public class RestClientApplication {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new MainForm(Locale.getDefault()).setVisible(true));
    }
}
