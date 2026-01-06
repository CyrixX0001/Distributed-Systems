package hostel.module5;

import javax.swing.*;
import java.awt.*;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MessFeedback extends JFrame {
    private JLabel lblCount = new JLabel("Votes: -");
    private JButton btnVote = new JButton("Vote Good");
    private static final String FILE = "shared.bin";

    public MessFeedback() {
        super("Shared Memory Feedback");
        setLayout(new FlowLayout());
        add(lblCount); add(btnVote);
        btnVote.addActionListener(e -> vote());
        new Timer(1000, e -> update()).start();
        setSize(300, 200); setVisible(true); setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void vote() {
        try (RandomAccessFile f = new RandomAccessFile(FILE, "rw"); FileChannel c = f.getChannel()) {
            c.lock();
            MappedByteBuffer m = c.map(FileChannel.MapMode.READ_WRITE, 0, 4);
            m.putInt(0, m.getInt(0) + 1);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void update() {
        try (RandomAccessFile f = new RandomAccessFile(FILE, "rw"); FileChannel c = f.getChannel()) {
            MappedByteBuffer m = c.map(FileChannel.MapMode.READ_WRITE, 0, 4);
            lblCount.setText("Votes: " + m.getInt(0));
        } catch (Exception e) {}
    }

    public static void main(String[] args) throws Exception {
        try (RandomAccessFile f = new RandomAccessFile(FILE, "rw")) {
            if (f.length() == 0) f.writeInt(0);
        }
        new MessFeedback();
    }
}