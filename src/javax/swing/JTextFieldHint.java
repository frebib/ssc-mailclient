package javax.swing;

import org.omg.PortableInterceptor.INACTIVE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

public class JTextFieldHint extends JTextField implements FocusListener, KeyListener {
    private String hint;
    private Font defaultFont;
    private Font lightFont;

    private final UIDefaults lf = UIManager.getLookAndFeel().getDefaults();
    private final Color INACTIVE_TEXT, ACTIVE_TEXT;

    public JTextFieldHint(String hint) {
        this(hint, null);
    }
    public JTextFieldHint(String hint, String text) {
        super();

        INACTIVE_TEXT = lf.getColor("TextField.inactiveForeground");
        ACTIVE_TEXT = lf.getColor("TextField.foreground");

        this.hint = hint;
        setText(hint);
        setForeground(INACTIVE_TEXT);


        this.addFocusListener(this);

        addKeyListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (getText().equals(hint))
            setText("");
        setForeground(ACTIVE_TEXT);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getText().equals(hint) || getText().isEmpty()) {
            setText(hint);
            setForeground(INACTIVE_TEXT);
        } else {
            setForeground(ACTIVE_TEXT);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setText(hint);
            setForeground(INACTIVE_TEXT);
            getParent().requestFocus();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
