package org.vaultdb.util;

import org.apache.calcite.util.Pair;
import org.apache.commons.io.FileUtils;

import org.bytedeco.javacpp.Loader;
import org.vaultdb.compiler.emp.EmpBuilder;
import org.vaultdb.config.SystemConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class EmpJniUtilities {

  public static String getPropertyFile() {
    String propertyFile =
        Utilities.getVaultDBRoot() + "/src/main/resources/org/bytedeco/javacpp/properties/";
    propertyFile += Loader.getPlatform();
    propertyFile += "-emp.properties";

    return propertyFile;
  }

  public static long convert(BitSet bits) {
    long value = 0L;
    for (int i = 0; i < bits.length(); ++i) {
      value += bits.get(i) ? (1L << i) : 0L;
    }
    return value;
  }


  // output handling
  public static List<String> revealStringOutput(String alice, String bob, int tupleWidth) {
    assert (alice.length() == bob.length());

    BitSet aliceBits = EmpJniUtilities.stringToBitSet(alice);
    BitSet bobBits = EmpJniUtilities.stringToBitSet(bob);

    int tupleBits = tupleWidth * 8; // 8 bits / char
    int tupleCount = alice.length() / tupleBits;

    try {
      Logger logger = SystemConfiguration.getInstance().getLogger();
      logger.info("Decrypting " + tupleCount + " tuples.");

    } catch (Exception e) {
      e.printStackTrace();
    }

    BitSet decrypted = (BitSet) aliceBits.clone();
    decrypted.xor(bobBits);

    List<String> output = new ArrayList<String>();

    int readIdx = 0;
    for (int i = 0; i < tupleCount; ++i) {
      BitSet bits = decrypted.get(readIdx * 8, (readIdx + tupleWidth) * 8);
      String tuple = deserializeString(bits, tupleWidth);
      output.add(tuple);
      readIdx += tupleWidth;
    }

    return output;
  }

  public static BitSet stringToBitSet(String s) {
    BitSet b = new BitSet(s.length());

    for (int i = 0; i < s.length(); ++i) {
      b.set(i, (s.charAt(i) == '1') ? true : false);
    }

    return b;
  }

  public static String deserializeString(BitSet src, int stringLength) {
    assert (src.size() % 8 == 0);

    String value = new String();

    for (int i = 0; i < stringLength; ++i) {
      int n = 0;
      for (int j = 0; j < 8; ++j) {
        boolean b = src.get(i * 8 + j);
        n = (n << 1) | (b ? 1 : 0);
      }

      value += (char) n;
    }

    return value;
  }


  // for debugging this does a deep delete on previous builds
  public static void cleanEmpCode(String className) throws Exception {
    Path generatedFiles = Paths.get(Utilities.getCodeGenTarget());
    deleteFilesForPathByPrefix(generatedFiles, className);

    String osCode = Utilities.getCodeGenTarget() + "/" + Loader.getPlatform();
    FileUtils.deleteDirectory(new File(osCode));

    String cache = System.getProperty("user.home") + "/.javacpp/cache";
    FileUtils.deleteDirectory(new File(cache));

    /*String deleteGeneratedClassFiles =
        "rm "
            + Utilities.getVaultDBRoot()
            + "/target/classes/org/vaultdb/compiler/emp/generated/"
            + className
            + "*";


    try {
    	Utilities.runCmd(deleteGeneratedClassFiles);
    } catch(Exception e) {
    	SystemConfiguration.getInstance().getLogger().info("EMP generated code cleanup snagged at: " + e.getMessage());
    	e.printStackTrace();
    }*/

  }

  private static void deleteFilesForPathByPrefix(final Path path, final String prefix) {
    try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(path, prefix + "*")) {
      for (final Path newDirectoryStreamItem : newDirectoryStream) {
        Files.delete(newDirectoryStreamItem);
      }
    } catch (final Exception e) {
    }
  }


  public static int getEmpPort() throws Exception {
    int port;
    // try local source
    String empPort = SystemConfiguration.getInstance().getProperty("emp-port");
    if (empPort != null && empPort != "") {
      port = Integer.parseInt(empPort); // TODO: check if it is numeric
    } else {
      // handle remote case
      port = Integer.parseInt(System.getProperty("emp.port"));
    }
    return port;
  }

  public static BitSet decrypt(BitSet alice, BitSet bob) {
    assert (alice.size() == bob.size());

    BitSet decrypted = (BitSet) alice.clone();
    decrypted.xor(bob);
    return decrypted;
  }

  public static BitSet decrypt(String alice, String bob) {
    assert (alice.length() == bob.length());

    BitSet aliceBits = stringToBitSet(alice);
    BitSet bobBits = stringToBitSet(bob);
    return decrypt(aliceBits, bobBits);
  }

  public static String getFullyQualifiedClassName(String className) throws Exception {
    String classPrefix = SystemConfiguration.getInstance().getProperty("generated-class-prefix");
    if (!className.startsWith(classPrefix)) {
      return classPrefix + "." + className;
    } else {
      return className;
    }
  }


	public static byte[] deserializeBooleans(boolean[] src) {
		int byteCount = (int) Math.ceil(src.length / 8.0);

		byte[] dst = new byte[byteCount];

		for(int i = 0; i < byteCount; ++i) {
			dst[i] = (byte)((src[i*8]?1<<7:0) + (src[i*8+1]?1<<6:0) + (src[i*8+2]?1<<5:0) +
	                (src[i*8+3]?1<<4:0) + (src[i*8+4]?1<<3:0) + (src[i*8+5]?1<<2:0) +
	                (src[i*8+6]?1<<1:0) + (src[i*8+7]?1:0));
		}

		return dst;
	}

//output handling
	public static List<String> revealOutput(boolean[] alice, boolean[] bob, int tupleWidth) {
		assert(alice.length == bob.length);
		boolean[] decrypted = new boolean[alice.length];
		int tupleBits = tupleWidth*8; // 8 bits / char
		int tupleCount = alice.length / tupleBits;
		List<String> output = new ArrayList<String>();



		for(int i = 0; i < alice.length; ++i) {
			decrypted[i] = alice[i] ^ bob[i];
		}


		byte[] allBytes = deserializeBooleans(decrypted);
		byte[] bytes;

		int readIdx = 0;
		for(int i = 0; i < tupleCount; ++i) {
			int fromIdx = readIdx;
			int toIdx = fromIdx + tupleWidth;

			bytes = Arrays.copyOfRange(allBytes, fromIdx, toIdx);
			String tuple = new String(bytes);

			output.add(tuple);
			readIdx = toIdx;
		}

		return output;
	}

  public static void buildEmpProgram(String className) throws Exception {

    EmpBuilder builder = new EmpBuilder(className);
    builder.compile();
  }
//	public static void buildEmpProgram(String className) throws Exception {
//		// fork and exec EmpBuilder to make it see the new files
//
//
//		SystemConfiguration config = SystemConfiguration.getInstance();
//		Logger logger = config.getLogger();
//		String mvnLocation = config.getProperty("maven-location");
//
//		String command =  mvnLocation +  " exec:java -Dexec.mainClass=\"org.vaultdb.compiler.emp.EmpBuilder\" -Dexec.args=\"" + className + "\"";
//		logger.info("EmpBuilder command: " + command);
//		Utilities.runCmd(command);
//
//
//
//	}
}
