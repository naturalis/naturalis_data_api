package nl.naturalis.nba.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * File utilities.
 * 
 * @author Ayco Holleman
 * @created Jul 23, 2015
 *
 */
public class FileUtil {

	private static final int DEFAULT_BUFFER_SIZE = 2048;
	private static final String UTF8 = "UTF-8";

	/**
	 * Create a {@code File} object representing the specified file in the
	 * specified directory.
	 * 
	 * @param directory
	 * @param fileName
	 * @return
	 */
	public static File newFile(File directory, String fileName)
	{
		return Paths.get(directory.getPath(), fileName).toFile();
	}

	public static InputStream newFileInputStream(File directory, String fileName)
	{
		return newFileInputStream(newFile(directory, fileName));
	}

	public static InputStream newFileInputStream(File file)
	{
		try {
			return new FileInputStream(file);
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean containsFile(File directory, String fileName)
	{
		File[] files = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isFile() && f.getName().equals(fileName);
			}
		});
		return files.length != 0;
	}

	public static boolean containsSubdirectory(File directory, String subdirName)
	{
		File[] files = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() && f.getName().equals(subdirName);
			}
		});
		return files.length != 0;
	}

	public static File[] getSubdirectories(File directory)
	{
		return directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory();
			}
		});
	}

	public static void convertToUtf8(String inputFile, String outputFile, String actualCharset)
	{
		convertEncoding(inputFile, outputFile, actualCharset, UTF8);
	}

	public static void convertToUtf8(File in, File out, String actualCharset)
	{
		convertEncoding(in, out, actualCharset, UTF8);
	}

	public static void convertEncoding(String inputFile, String outputFile, String actualCharset,
			String desiredCharset)
	{
		convertEncoding(new File(inputFile), new File(outputFile), actualCharset, desiredCharset);
	}

	@Deprecated
	public static void convertEncoding_old(File in, File out, String actualCharset,
			String desiredCharset)
	{

		int size = (int) Math.min(8192, in.length());
		byte[] buf = new byte[size];
		try (FileInputStream fis = new FileInputStream(in);
				FileOutputStream fos = new FileOutputStream(out);) {
			int numBytes = fis.read(buf);
			while (numBytes != -1) {
				String unconverted = new String(buf, 0, numBytes, actualCharset);
				String converted = new String(unconverted.getBytes(desiredCharset));
				fos.write(converted.getBytes());
				fos.flush();
				numBytes = fis.read(buf);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void convertEncoding(File in, File out, String actualCharset,
			String desiredCharset)
	{
		try (FileInputStream fis = new FileInputStream(in);
				FileOutputStream fos = new FileOutputStream(out);) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(fis, Charset.forName(actualCharset)));
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(fos, Charset.forName(desiredCharset)));
			char[] buffer = new char[8192];
			int charsRead;
			while ((charsRead = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, charsRead);
				writer.flush();
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the contents of a file. The character encoding of the file is assumed
	 * to be UTF-8.
	 * 
	 * @param path
	 *            The full path to the file
	 * 
	 * @return The contents of the file
	 */
	public static String getContents(String path)
	{
		return getContents(path, UTF8);
	}

	/**
	 * Get the contents of a file.
	 * 
	 * @param path
	 *            The full path to the file
	 * @param charset
	 *            The character encoding of the file
	 * 
	 * @return The contents of the file
	 */
	public static String getContents(String path, String charset)
	{
		return new String(getByteContents(path), Charset.forName(charset));
	}

	public static byte[] getByteContents(String path)
	{
		Path p = FileSystems.getDefault().getPath(path);
		try {
			return Files.readAllBytes(p);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the contents of the specified file. The character encoding of the
	 * file is assumed to be UTF-8.
	 * 
	 * @param file
	 *            The file to read
	 * 
	 * @return The contents of the file
	 */
	public static String getContents(File file)
	{
		return getContents(file.getPath());
	}

	/**
	 * Get the contents of the specified file.
	 * 
	 * @param file
	 *            The file to read
	 * @param charset
	 *            The character encoding of the file
	 * 
	 * @return The contents of the file
	 */
	public static String getContents(File file, String charset)
	{
		return getContents(file.getPath(), charset);
	}

	/**
	 * Get the contents of the specified resource. The character encoding of the
	 * resource is assumed to be UTF-8.
	 * 
	 * @param url
	 *            The resource
	 * 
	 * @return The contents of the resource
	 */
	public static String getContents(InputStream is)
	{
		return StringUtil.fromInputStream(is);
	}

	/**
	 * Get the contents of the specified resource.
	 * 
	 * @param url
	 *            The resource
	 * @param charset
	 *            The character encoding of the resource
	 * 
	 * @return The contents of the resource
	 */
	public static String getContents(URL url, String charset)
	{
		return getContents(new File(URI.create(url.toString())).getPath(), charset);
	}

	///////////////////////////////////////////////////////////////
	// SET FILE CONTENTS
	///////////////////////////////////////////////////////////////

	public static void setContents(String path, String contents)
	{
		setContents(new File(path), contents, UTF8);
	}

	public static void setContents(String path, String contents, String charset)
	{
		setContents(new File(path), contents, charset);
	}

	public static void setContents(String path, byte[] contents)
	{
		setContents(new File(path), new ByteArrayInputStream(contents), contents.length, false);
	}

	public static void setContents(File file, String contents)
	{
		setContents(file, contents, UTF8);
	}

	public static void setContents(File file, String contents, String charset)
	{
		byte[] bytes = contents.getBytes(Charset.forName(charset));
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		setContents(file, bais, bytes.length, false);
	}

	public static void setContents(File file, InputStream contents)
	{
		setContents(file, contents, DEFAULT_BUFFER_SIZE, false);
	}

	public static void setContents(File file, InputStream contents, int bufsize)
	{
		setContents(file, contents, bufsize, false);
	}

	public static void setContents(File file, byte[] contents)
	{
		setContents(file, new ByteArrayInputStream(contents), contents.length, false);
	}

	///////////////////////////////////////////////////////////////
	// ADD CONTENT
	///////////////////////////////////////////////////////////////

	public static void addContent(String path, String contents)
	{
		addContent(new File(path), contents, UTF8);
	}

	public static void addContent(String path, String contents, String charset)
	{
		addContent(new File(path), contents, charset);
	}

	public static void addContent(File file, String content)
	{
		addContent(file, content, UTF8);
	}

	public static void addContent(File file, String content, String charset)
	{
		byte[] bytes = content.getBytes(Charset.forName(charset));
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		setContents(file, bais, DEFAULT_BUFFER_SIZE, true);
	}

	public static void addContent(File file, InputStream contents, int bufsize)
	{
		setContents(file, contents, bufsize, true);
	}

	public static void addContent(File file, byte[] content)
	{
		setContents(file, new ByteArrayInputStream(content), content.length, true);
	}

	///////////////////////////////////////////////////////////////
	// SET / ADD CONTENT
	///////////////////////////////////////////////////////////////

	/**
	 * Exhausts the specified input stream and writes the data read from it to
	 * the specified file. The input stream is not closed once all data is read.
	 * 
	 * @param file
	 *            The file to write to
	 * @param contents
	 *            The input stream to
	 * @param bufsize
	 *            The byte size of the output buffer
	 * @param append
	 *            Whether to append to or replace the original file contents.
	 */
	public static void setContents(File file, InputStream contents, int bufsize, boolean append)
	{
		BufferedOutputStream bos = null;
		try {
			FileOutputStream fos;
			if (append) {
				if (!file.exists()) {
					file.createNewFile();
				}
				fos = new FileOutputStream(file, true);
			}
			else {
				Files.deleteIfExists(file.toPath());
				file.createNewFile();
				fos = new FileOutputStream(file, false);
			}
			bos = new BufferedOutputStream(fos, bufsize);
			if (!(contents instanceof BufferedInputStream
					|| contents instanceof ByteArrayInputStream)) {
				contents = new BufferedInputStream(contents, bufsize);
			}
			byte[] data = new byte[bufsize];
			int n = contents.read(data, 0, data.length);
			while (n != -1) {
				bos.write(data, 0, n);
				n = contents.read(data, 0, data.length);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * A very crude logging mechanism, mainly for verifying or debugging the
	 * operation of real logging libraries. The message is written to
	 * org.domainobject.util.FileUtil.log in the temp directory as well as
	 * printed to standard out.
	 * 
	 * @see #log(String, String, Object...)
	 * 
	 * @param file
	 *            The file to append the message to
	 * @param message
	 *            The message to log
	 */
	public static void log(String message, Object... messageArgs)
	{
		String path = System.getProperty("java.io.tmpdir") + '/' + FileUtil.class.getName()
				+ ".log";
		log(new File(path), message, messageArgs);
	}

	private static final SimpleDateFormat LOGDATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * A very crude logging mechanism, mainly for verifying or debugging the
	 * operation of real logging libraries. The {@code message} argument will be
	 * prefixed with a newline, a timestamp and fed into {@code String.format}
	 * along with the {@code messageArgs} array.
	 * 
	 * @param file
	 *            The file to append the message to
	 * @param message
	 *            The message to log
	 */
	public static void log(File file, String message, Object... messageArgs)
	{
		String nl = System.getProperty("line.separator");
		String time = LOGDATE_FORMAT.format(new Date());
		message = nl + time + " " + String.format(message, messageArgs);
		System.out.println(message);
		addContent(file, message);
	}

	/**
	 * Deletes the specified file. A {@code RuntimeException} will be thrown if
	 * the file could not be deleted.
	 * 
	 * @param path
	 *            The path to the file
	 * 
	 */
	public static void delete(String path)
	{
		try {
			Files.deleteIfExists(FileSystems.getDefault().getPath(path));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
