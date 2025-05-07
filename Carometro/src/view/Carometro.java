package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.awt.Color;
import java.awt.Desktop;

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
	private JButton btnAdicionar;
	private JButton btnEditar;
	private JButton btnExcluir;
	private JButton btnReset;
	private JButton btnBuscar;
	private JButton btnCarregar;
	private JButton btnSobre;
	private JButton btnPDF;

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
		
		listNomes = new JList<String>();
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
		panel.setBounds(0, 300, 722, 79);
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
					int confirma = JOptionPane.showConfirmDialog(null, "Aluno não cadastrado!\nDeseja cadastrar este aluno?", "Aviso", JOptionPane.YES_NO_OPTION);
					if (confirma == JOptionPane.YES_OPTION) {
						txtRA.setEditable(false);
						btnBuscar.setEnabled(false);
						btnCarregar.setEnabled(true);
						btnAdicionar.setEnabled(true);
						btnPDF.setEnabled(false);
					} else {
						reset();
					}
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
		
		btnCarregar = new JButton("Carregar foto");
		btnCarregar.setEnabled(false);
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
		
		btnAdicionar = new JButton("");
		btnAdicionar.setEnabled(false);
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
		btnAdicionar.setBounds(18, 204, 64, 64);
		contentPane.add(btnAdicionar);
		
		btnReset = new JButton("");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		btnReset.setToolTipText("Limpar campos");
		btnReset.setIcon(new ImageIcon(Carometro.class.getResource("/img/vassoura.png")));
		btnReset.setBounds(322, 204, 64, 64);
		contentPane.add(btnReset);
		
		btnBuscar = new JButton("Buscar");
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
		
		btnEditar = new JButton("");
		btnEditar.setEnabled(false);
		btnEditar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editar();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnEditar.setToolTipText("Editar");
		btnEditar.setIcon(new ImageIcon(Carometro.class.getResource("/img/reload.png")));
		btnEditar.setBounds(94, 204, 64, 64);
		contentPane.add(btnEditar);
		
		btnExcluir = new JButton("");
		btnExcluir.setEnabled(false);
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
		btnExcluir.setBounds(170, 204, 64, 64);
		contentPane.add(btnExcluir);
		
		JLabel lblBusca = new JLabel("");
		lblBusca.setIcon(new ImageIcon(Carometro.class.getResource("/img/search2.png")));
		lblBusca.setBounds(332, 79, 20, 20);
		contentPane.add(lblBusca);
		
		btnSobre = new JButton("");
		btnSobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Sobre sobre = new Sobre();
				sobre.setVisible(true);
			}
		});
		btnSobre.setContentAreaFilled(false);
		btnSobre.setBorderPainted(false);
		btnSobre.setIcon(new ImageIcon(Carometro.class.getResource("/img/about.png")));
		btnSobre.setBounds(359, 12, 48, 48);
		contentPane.add(btnSobre);
		
		btnPDF = new JButton("");
		btnPDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gerarPDF();
			}
		});
		btnPDF.setIcon(new ImageIcon(Carometro.class.getResource("/img/pdf.png")));
		btnPDF.setToolTipText("Gerar lista de alunos");
		btnPDF.setBounds(246, 204, 64, 64);
		contentPane.add(btnPDF);
		this.setLocationRelativeTo(null);
	}
	
	@SuppressWarnings("unused")
	private void status() throws SQLException {
		try {
			con = dao.conectar();
			if (con == null) {
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/error.png")));
			} else {
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
					txtRA.setEditable(false);
					btnBuscar.setEnabled(false);
					btnCarregar.setEnabled(true);
					btnEditar.setEnabled(true);
					btnExcluir.setEnabled(true);
					btnPDF.setEnabled(false);
				} else {
					int confirma = JOptionPane.showConfirmDialog(null, "Aluno não cadastrado!\nDeseja iniciar um novo cadastro?", "Aviso", JOptionPane.YES_NO_OPTION);
					if (confirma == JOptionPane.YES_OPTION) {
						txtRA.setEditable(false);
						btnBuscar.setEnabled(false);
						txtNome.setText(null);
						txtNome.requestFocus();
						btnCarregar.setEnabled(true);
						btnAdicionar.setEnabled(true);
					} else {
						reset();
					}
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
					lblFoto.setIcon(foto);
					txtRA.setEditable(false);
					btnBuscar.setEnabled(false);
					btnCarregar.setEnabled(true);
					btnEditar.setEnabled(true);
					btnExcluir.setEnabled(true);
					btnPDF.setEnabled(false);
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
	
	private void gerarPDF() {
		Document document = new Document();
		
		// gerar documento pdf
		try {
			PdfWriter.getInstance(document, new FileOutputStream("alunos.pdf"));
			document.open();
			Date data = new Date();
			DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL);
			document.add(new Paragraph(formatador.format(data)));
			document.add(new Paragraph("Listagem de alunos: "));
			document.add(new Paragraph(" "));
			// tabela
			PdfPTable tabela = new PdfPTable(3);
			PdfPCell coluna1 = new PdfPCell(new Paragraph("RA"));
			tabela.addCell(coluna1);
			PdfPCell coluna2 = new PdfPCell(new Paragraph("Nome"));
			tabela.addCell(coluna2);
			PdfPCell coluna3 = new PdfPCell(new Paragraph("Foto"));
			tabela.addCell(coluna3);
			String readLista = "select * from alunos order by nome";
			try {
				con = dao.conectar();
				pst = con.prepareStatement(readLista);
				rs = pst.executeQuery();
				while (rs.next()) {
					tabela.addCell(rs.getString(1));
					tabela.addCell(rs.getString(2));
					Blob blob = rs.getBlob(3);
					byte[] img = blob.getBytes(1, (int) blob.length());
					com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(img);
					tabela.addCell(image);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				con.close();
			}
			document.add(tabela);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			document.close();
		}
		// abrir o documento pdf no leitor
		try {
			Desktop.getDesktop().open(new File("alunos.pdf"));
		} catch (Exception e2) {
			System.out.println(e2);
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
		txtRA.setEditable(true);
		btnBuscar.setEnabled(true);
		btnCarregar.setEnabled(false);
		btnAdicionar.setEnabled(false);
		btnEditar.setEnabled(false);
		btnExcluir.setEnabled(false);
		btnPDF.setEnabled(true);
	}
}
