package edu.colostate.vchill.gui;

import edu.colostate.vchill.ViewControl;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Right-click menu for file browser
 *
 * @author Jochen Deyke
 * @version 2007-09-26
 */
public class ViewFileBrowserPopup extends JPopupMenu {
    /**
     *
     */
    private static final long serialVersionUID = 3753353267231692133L;
    protected static final edu.colostate.vchill.Config config = edu.colostate.vchill.Config.getInstance();
    protected static final ViewControl vc = ViewControl.getInstance();

    public static final AbstractAction archiveAction = new AbstractAction("Connect to Archive Server") {
        /**
         *
         */
        private static final long serialVersionUID = -98966020254833126L;

        public void actionPerformed(final ActionEvent ae) {
            final JComboBox server = new JComboBox(config.getSocketHistory());
            server.setEditable(true);
            final JLabel serverLabel = new JLabel("Archive Server: ");
            serverLabel.setDisplayedMnemonic('A');
            serverLabel.setLabelFor(server);
            JOptionPane pane = new JOptionPane(new Object[]{
                    "Which server do you want to connect to?\n",
                    serverLabel, server,
            }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            pane.setWantsInput(false);
            JDialog dialog = pane.createDialog(null, "Which server?");
            dialog.pack();
            dialog.setVisible(true);
            Integer value = (Integer) pane.getValue();
            if (value == null || value.intValue() == JOptionPane.CANCEL_OPTION || value.intValue() == JOptionPane.CLOSED_OPTION)
                return;
            String socketName = (String) server.getSelectedItem();
            config.addToSocketHistory(socketName);
            vc.connect(socketName);
        }
    };

    public static final AbstractAction realtimeAction = new AbstractAction("Connect to Realtime Data") {
        /**
         *
         */
        private static final long serialVersionUID = 3822242288577985415L;

        public void actionPerformed(final ActionEvent ae) {
            //determine server to connect to
            final JComboBox control = new JComboBox(config.getControlHistory());
            control.setEditable(true);
            final JLabel controlLabel = new JLabel("Antenna Control Server: ");
            controlLabel.setDisplayedMnemonic('C');
            controlLabel.setLabelFor(control);
            final JComboBox data = new JComboBox(config.getRealtimeHistory());
            data.setEditable(true);
            final JLabel dataLabel = new JLabel("Realtime Data Server: ");
            dataLabel.setDisplayedMnemonic('D');
            dataLabel.setLabelFor(data);
            JOptionPane pane = new JOptionPane(new Object[]{
                    "Which servers do you want to connect to?\n\n",
                    controlLabel, control, "\n",
                    dataLabel, data,
            }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            pane.setWantsInput(false);
            JDialog dialog = pane.createDialog(null, "Which servers?");
            dialog.pack();
            dialog.setVisible(true);
            Integer value = (Integer) pane.getValue();
            if (value == null || value.intValue() == JOptionPane.CANCEL_OPTION || value.intValue() == JOptionPane.CLOSED_OPTION)
                return;
            String controlName = (String) control.getSelectedItem();
            config.addToControlHistory(controlName);
            String dataName = (String) data.getSelectedItem();
            config.addToRealtimeHistory(dataName);
            vc.startRadarControl(controlName, dataName);
        }
    };

    public static final AbstractAction filesystemAction = new AbstractAction("Connect to Filesystem") {
        /**
         *
         */
        private static final long serialVersionUID = -4241158884461310333L;
        private File lastDir = null;

        public void actionPerformed(final ActionEvent ae) {
            JFileChooser chooser = new JFileChooser(this.lastDir);
            chooser.setDialogTitle("Select the data files and/or directories to connect");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) for (File selection : chooser.getSelectedFiles()) {
                File currDir = selection.isDirectory() ? selection : selection.getParentFile();
                vc.addFile(currDir);
                this.lastDir = currDir;
            }
        }
    };

    private final ViewFileBrowserActions actions;
    private final int[] coords = new int[2]; //x, y

    public ViewFileBrowserPopup(final ViewFileBrowserActions actions) {
        super("Connections");
        this.actions = actions;

        for (JMenuItem item : ViewFileBrowserPopup.createConnectionItems()) this.add(item);

        this.addSeparator();

        JMenuItem disconnect = new JMenuItem(new AbstractAction("Disconnect") {
            /**
             *
             */
            private static final long serialVersionUID = -953358098757259980L;

            public void actionPerformed(final ActionEvent ae) {
                TreePath path = actions.getPathForLocation(coords[0], coords[1]);
                if (path == null || path.getPathCount() < 2) {
                    vc.disconnect();
                    return;
                }
                vc.disconnect(path.getPathComponent(1).toString());
            }
        }) {
            /**
             *
             */
            private static final long serialVersionUID = 6974753682898451817L;

            public void paint(final java.awt.Graphics g) {
                TreePath path = actions.getPathForLocation(coords[0], coords[1]);
                setEnabled((path == null || path.getPathCount() < 2) ?
                        vc.isConnected() :
                        vc.isConnected((String) ((FileTreeNode) path.getPathComponent(1)).getUserObject()));
                super.paint(g);
            }
        };
        disconnect.setMnemonic(KeyEvent.VK_D);
        this.add(disconnect);

        JMenuItem reconnect = new JMenuItem(new AbstractAction("Reconnect") {
            /**
             *
             */
            private static final long serialVersionUID = 6784258653395638485L;

            public void actionPerformed(final ActionEvent ae) {
                TreePath path = actions.getPathForLocation(coords[0], coords[1]);
                if (path == null || path.getPathCount() < 2) {
                    vc.reconnect();
                    return;
                }
                vc.reconnect((String) ((FileTreeNode) path.getPathComponent(1)).getUserObject());
            }
        }) {
            /**
             *
             */
            private static final long serialVersionUID = 5882949101599876134L;

            public void paint(final java.awt.Graphics g) {
                TreePath path = actions.getPathForLocation(coords[0], coords[1]);
                setEnabled((path == null || path.getPathCount() < 2) ?
                        vc.getControlMessage().getURL() != null :
                        vc.isConnected((String) ((FileTreeNode) path.getPathComponent(1)).getUserObject()));
                super.paint(g);
            }
        };
        reconnect.setMnemonic(KeyEvent.VK_R);
        this.add(reconnect);

        this.addSeparator();

        JMenuItem refresh = new JMenuItem(new AbstractAction("Refresh Connection List") {
            /**
             *
             */
            private static final long serialVersionUID = -4112787439392835443L;

            public void actionPerformed(final ActionEvent ae) {
                actions.refreshConnections();
            }
        });
        refresh.setMnemonic(KeyEvent.VK_L);
        this.add(refresh);

        JMenuItem bookmark = new JMenuItem(new AbstractAction("Create Bookmark") {
            /**
             *
             */
            private static final long serialVersionUID = 6097877360581319617L;

            public void actionPerformed(final ActionEvent ae) {
                vc.createBookmark(ViewFileBrowserActions.getControlMessage(actions.getPathForLocation(coords[0], coords[1])));
            }
        });
        bookmark.setMnemonic(KeyEvent.VK_B);
        this.add(bookmark);
    }

    static class ConnectionItem extends JMenuItem {
        /**
         *
         */
        private static final long serialVersionUID = 8659304815026174713L;

        public ConnectionItem(final AbstractAction aa) {
            super(aa);
        }

        public void paint(final java.awt.Graphics g) {
            setEnabled(!config.isRealtimeModeEnabled());
            super.paint(g);
        }
    }

    static JMenuItem[] createConnectionItems() {
        JMenuItem archive = new ConnectionItem(archiveAction);
        archive.setMnemonic(KeyEvent.VK_A);

        JMenuItem realtime = new ConnectionItem(realtimeAction);
        realtime.setMnemonic(KeyEvent.VK_R);

        JMenuItem filesystem = new ConnectionItem(filesystemAction);
        filesystem.setMnemonic(KeyEvent.VK_F);

        return new JMenuItem[]{archive, realtime, filesystem};
    }

    public void show(final Component invoker, final int x, final int y) {
        super.show(invoker, coords[0] = x, coords[1] = y);
    }
}
