package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.DAO;
import utils.Validador;

import java.awt.Toolkit;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Carometro extends JFrame {
	
	DAO dao = new DAO();
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	
	private FileInputStream fis;
	private int tamanho;
	private boolean fotoCarregada = false;
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblStatus;
	private JLabel lblData;
	private JTextField txtRA;
	private JLabel lblNewLabel_1;
	private JTextField txtNome;
	private JLabel lblFoto;
	private JList<String> listNomes;
	private JScrollPane scrollPaneLista;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Carometro frame = new Carometro();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Carometro() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				try {
					status();
					setarData();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		setTitle("Carometro");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Carometro.class.getResource("/img/instagram.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 407);
		contentPane = new JPanel();
		contentPane.setToolTipText("Adicionar");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPaneLista = new JScrollPane();
		scrollPaneLista.setBorder(null);
		scrollPaneLista.setVisible(false);
		scrollPaneLista.setBounds(105, 100, 221, 79);
		contentPane.add(scrollPaneLista);
		
		listNomes = new JList();
		listNomes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					buscarNome();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		listNomes.setBorder(null);
		scrollPaneLista.setViewportView(listNomes);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 0, 0));
		panel.setBounds(0, 299, 771, 79);
		contentPane.add(panel);
		panel.setLayout(null);
		
		lblStatus = new JLabel("");
		lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/error.png")));
		lblStatus.setBounds(655, 24, 32, 32);
		panel.add(lblStatus);
		
		lblData = new JLabel("");
		lblData.setForeground(SystemColor.text);
		lblData.setFont(new Font("Dialog", Font.BOLD, 14));
		lblData.setBounds(22, 41, 199, 15);
		panel.add(lblData);
		
		JLabel lblNewLabel = new JLabel("RA");
		lblNewLabel.setBounds(53, 28, 24, 15);
		contentPane.add(lblNewLabel);
		
		txtRA = new JTextField();
		txtRA.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String caracteres = "0123456789";
				if (!caracteres.contains(e.getKeyChar() + "")) {
					e.consume();
				}
			}
		});
		txtRA.setBounds(105, 26, 106, 19);
		contentPane.add(txtRA);
		txtRA.setColumns(10);
		// limita os campos
		txtRA.setDocument(new Validador(6));
		
		lblNewLabel_1 = new JLabel("Nome");
		lblNewLabel_1.setBounds(53, 84, 51, 15);
		contentPane.add(lblNewLabel_1);
		
		txtNome = new JTextField();
		txtNome.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtNome.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					listarNomes();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					scrollPaneLista.setVisible(false);
				}
			}
		});
		txtNome.setBounds(105, 82, 221, 19);
		contentPane.add(txtNome);
		txtNome.setColumns(10);
		// limita os campos
		txtNome.setDocument(new Validador(30));
		
		lblFoto = new JLabel("");
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/camera2.png")));
		lblFoto.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lblFoto.setBounds(440, 12, 256, 256);
		contentPane.add(lblFoto);
		
		JButton btnCarregar = new JButton("Carregar foto");
		btnCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregarFoto();
			}
		});
		btnCarregar.setForeground(SystemColor.textHighlight);
		btnCarregar.setBounds(187, 128, 139, 25);
		contentPane.add(btnCarregar);
		
		JLabel label = new JLabel("");
		label.setBounds(72, 200, 51, 15);
		contentPane.add(label);
		
		JButton btnAdicionar = new JButton("");
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					adicionar();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnAdicionar.setToolTipText("Adicionar");
		btnAdicionar.setIcon(new ImageIcon(Carometro.class.getResource("/img/add.png")));
		btnAdicionar.setBounds(39, 204, 64, 64);
		contentPane.add(btnAdicionar);
		
		JButton btnReset = new JButton("");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		btnReset.setToolTipText("Limpar campos");
		btnReset.setIcon(new ImageIcon(Carometro.class.getResource("/img/vassoura.png")));
		btnReset.setBounds(305, 204, 64, 64);
		contentPane.add(btnReset);
		
		JButton btnBuscar = new JButton("Buscar");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					buscarRA();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnBuscar.setForeground(SystemColor.textHighlight);
		btnBuscar.setBounds(228, 23, 90, 25);
		contentPane.add(btnBuscar);
		
		JButton btnAdicionar_1 = new JButton("");
		btnAdicionar_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editar();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnAdicionar_1.setToolTipText("Editar");
		btnAdicionar_1.setIcon(new ImageIcon(Carometro.class.getResource("/img/reload.png")));
		btnAdicionar_1.setBounds(127, 204, 64, 64);
		contentPane.add(btnAdicionar_1);
		
		JButton btnExcluir = new JButton("");
		btnExcluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					excluir();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnExcluir.setToolTipText("Excluir");
		btnExcluir.setIcon(new ImageIcon(Carometro.class.getResource("/img/excluir.png")));
		btnExcluir.setBounds(215, 204, 64, 64);
		contentPane.add(btnExcluir);
	}
	
	@SuppressWarnings("unused")
	private void status() throws SQLException {
		try {
			con = dao.conectar();
			if (con == null) {
				// System.out.println("Erro de conexão!");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/error.png")));
			} else {
				// System.out.println("Banco de dados conectado!");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/on.png")));
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			con.close();
		}
	}
	
	private void setarData() {
		Date data = new Date();
		DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL);
		lblData.setText(formatador.format(data));
	}
	
	private void carregarFoto() {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Selecionar arquivo");
		jfc.setFileFilter(new FileNameExtensionFilter("Arquivo de imagens(*.PNG, *.JPG, *.JPEG)", "jpg", "png", "jpeg"));
		int resultado = jfc.showOpenDialog(this);
		if (resultado == JFileChooser.APPROVE_OPTION) {
			try {
				fis = new FileInputStream(jfc.getSelectedFile());
				tamanho = (int) jfc.getSelectedFile().length();
				Image foto = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH);
				lblFoto.setIcon(new ImageIcon(foto));
				fotoCarregada = true;
			} catch (Exception e) {
				// JOptionPane.showMessageDialog(this, "Erro ao caregar imagem: " + e);
				System.out.println(e);
			}
		}
	}
	
	private void adicionar() throws SQLException {
		if (txtNome.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Preencha o nome!");
			txtNome.requestFocus(); // posiciona o cursor no campo
		} else if (tamanho == 0) {
			JOptionPane.showMessageDialog(null, "Selecione a foto!");
		} else {
			String insert = "insert into alunos (nome, foto) values(?, ?)";
			try {
				con = dao.conectar();
				pst = con.prepareStatement(insert);
				pst.setString(1, txtNome.getText());
				pst.setBlob(2, fis, tamanho);
				int confirma = pst.executeUpdate();
				if (confirma == 1) {
					JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso!");
					reset();
				} else {
					JOptionPane.showMessageDialog(null, "Aluno não cadastrado!");
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				con.close();
			}
		}
	}
	
	private void buscarRA() throws SQLException {
		if (txtRA.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Digite o RA!");
			txtRA.requestFocus();
		} else {
			String readRA = "select * from alunos where ra = ?";
			try {
				con = dao.conectar();
				pst = con.prepareStatement(readRA);
				pst.setString(1, txtRA.getText());
				rs = pst.executeQuery();
				if (rs.next()) {
					txtNome.setText(rs.getString(2));
					Blob blob = rs.getBlob(3);
					byte[] img = blob.getBytes(1, (int) blob.length());
					BufferedImage imagem = null;
					try {
						imagem = ImageIO.read(new ByteArrayInputStream(img));
					} catch (Exception e) {
						System.out.println(e);
					}
					ImageIcon icone = new ImageIcon(imagem);
					ImageIcon foto = new ImageIcon(icone.getImage().getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH));
					lblFoto.setIcon(foto);
				} else {
					JOptionPane.showMessageDialog(null, "Aluno não cadastrado!");
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				con.close();
			}
		}
	}
	
	private void listarNomes() throws SQLException {
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		listNomes.setModel(modelo);
		String readLista = "select * from alunos where nome like '" + txtNome.getText() + "%' order by nome;";
		try {
			con = dao.conectar();
			pst = con.prepareStatement(readLista);
			rs = pst.executeQuery();
			while (rs.next()) {
				scrollPaneLista.setVisible(true);
				modelo.addElement(rs.getString(2));
				if (txtNome.getText().isEmpty()) {
					scrollPaneLista.setVisible(false);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			con.close();
		}
	}
	
	private void buscarNome() throws SQLException {
		int linha = listNomes.getSelectedIndex();
		if (linha >= 0) {
			String readNome = "select * from alunos where nome like '" + txtNome.getText() + "%' order by nome limit " + (linha) + ", 1";
			try {
				con = dao.conectar();
				pst = con.prepareStatement(readNome);
				rs = pst.executeQuery();
				while (rs.next()) {
					scrollPaneLista.setVisible(false);
					txtRA.setText(rs.getString(1));
					txtNome.setText(rs.getString(2));
					Blob blob = rs.getBlob(3);
					byte[] img = blob.getBytes(1, (int) blob.length());
					BufferedImage imagem = null;
					try {
						imagem = ImageIO.read(new ByteArrayInputStream(img));
					} catch (Exception e) {
						System.out.println(e);
					}
					ImageIcon icone = new ImageIcon(imagem);
					ImageIcon foto = new ImageIcon(icone.getImage().getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH));
					lblFoto.setIcon(foto);
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				con.close();
			}
		} else {
			scrollPaneLista.setVisible(false);
		}
	}
	
	private void editar() throws SQLException {
		if (txtNome.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Preencha o nome!");
			txtNome.requestFocus(); // posiciona o cursor no campo
		} else {
			if (fotoCarregada == true) {
				String update = "update alunos set nome = ?, foto = ? where ra = ?";
				try {
					con = dao.conectar();
					pst = con.prepareStatement(update);
					pst.setString(1, txtNome.getText());
					pst.setBlob(2, fis, tamanho);
					pst.setString(3, txtRA.getText());
					int confirma = pst.executeUpdate();
					if (confirma == 1) {
						JOptionPane.showMessageDialog(null, "Dados do aluno alterados com sucesso!");
						reset();
					} else {
						JOptionPane.showMessageDialog(null, "Dados não alterados!");
					}
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					con.close();
				}
			} else {
				String update = "update alunos set nome = ? where ra = ?";
				try {
					con = dao.conectar();
					pst = con.prepareStatement(update);
					pst.setString(1, txtNome.getText());
					pst.setString(2, txtRA.getText());
					int confirma = pst.executeUpdate();
					if (confirma == 1) {
						JOptionPane.showMessageDialog(null, "Dados do aluno alterados com sucesso!");
						reset();
					} else {
						JOptionPane.showMessageDialog(null, "Dados não alterados!");
					}
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					con.close();
				}
			}
		}
	}
	
	private void excluir() throws SQLException {
		int confirmaExcluir = JOptionPane.showConfirmDialog(null, "Confirma a exclusão desse aluno?", "Atenção!", JOptionPane.YES_NO_OPTION);
		if (confirmaExcluir == JOptionPane.YES_OPTION) {
			String delete = "delete from alunos where ra = ?";
			try {
				con = dao.conectar();
				pst = con.prepareStatement(delete);
				pst.setString(1, txtRA.getText());
				int confirma = pst.executeUpdate();
				if (confirma == 1) {
					reset();
					JOptionPane.showMessageDialog(null, "Aluno excluído com sucesso!");
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				con.close();
			}
		}
	}
	
	private void reset() {
		scrollPaneLista.setVisible(false);
		txtRA.setText(null);
		txtNome.setText(null);
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/camera2.png")));
		txtNome.requestFocus();
		fotoCarregada = false;
		tamanho = 0;
	}
}
