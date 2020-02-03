import java.io.IOException;
import java.util.List;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        CSVwriter csv = new CSVwriter("database.csv");

//        csv.addElementToDatabase("User",
//                new String[]{"Id","Name","Surname","email","password"},
//                new String[]{"12345","Tomasz","Lewandowski","lewy@gmail.com","javiarenka123"});

        csv.addElementToDatabase("User",
                new String[]{"42069","Szymon","Ilnicki","igieu@gmail.com","ngssie666"},
                false);

        csv.addElementToDatabase("User",
                new String[]{"99999","Szymon","Testowski","test@gmail.com","123test"},
                false);

        UserModel users = new UserModel();
        List<UserTmp> search = users.getUsersByName("Szymon");

        users.addUser("Tomasz", "Nazwisko", "placeholder@gmail.com", "xxx");

    }
}
