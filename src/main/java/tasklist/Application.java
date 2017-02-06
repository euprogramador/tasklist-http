package tasklist;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {
	private static String KEY = "";

	@RequestMapping("/")
	public ResponseEntity<?> tasklist(@RequestParam(name="key",required=false) String key)
			throws Exception {
		if (!KEY.equals(key)) {
			return new ResponseEntity<String>("",HttpStatus.BAD_REQUEST);
		}
		try {
			Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv",
					"/nh").start();
			Scanner sc = new Scanner(process.getInputStream());
			StringBuilder result = new StringBuilder();
			result.append("[");

			if (sc.hasNextLine())
				sc.nextLine();

			while (sc.hasNextLine()) {

				String line = sc.nextLine();
				String[] parts = line.split(",");
				String unq = parts[0].substring(1).replaceFirst(".$", "");
				String pid = parts[1].substring(1).replaceFirst(".$", "");
				result.append(unq);
				// System.out.println(unq + " " + pid);
			}
			sc.close();
			result.append("]");
			process.waitFor();
			return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Informe a chave usada para o servidor");
			System.exit(-1);
		}

		KEY = args[0];

		SpringApplication.run(Application.class, args);
	}
}
