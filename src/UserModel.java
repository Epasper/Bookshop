import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserModel
{
    private List<UserTmp> usersList = new ArrayList<UserTmp>();
    private String userClassName = "User";
    private String[] descriptions = new String[]{"Id", "Name", "Surname", "email", "password"};
    private String databaseName = "database.csv";

    public UserModel() throws IOException
    {
        String[][] userData;

        CSVwriter database = new CSVwriter(databaseName);
        Scanner myReader;

        if (database.doesClassExist(userClassName))
        {
            userData = database.getElementsOfClass(userClassName, false);

            for (int i = 0; i < userData.length; i++)
            {
                usersList.add(new UserTmp(userData[i]));
            }

        }
        else
        {
            database.addNewClassWithDescriptions("User", descriptions);
        }
    }

    UserTmp getUserById(String searchedId)
    {
        UserTmp currentUser;
        for (int i = 0; i < usersList.size(); i++)
        {
            currentUser = usersList.get(i);
            if (currentUser.getId().equals(searchedId))
            {
                return currentUser;
            }
        }
        return null;
    }

    boolean isIdInUse(String searchedId)
    {
        return !(getUserById(searchedId) == null);
    }

    UserTmp getUserByEmail(String searchedEmail)
    {
        UserTmp currentUser;
        for (int i = 0; i < usersList.size(); i++)
        {
            currentUser = usersList.get(i);
            if (currentUser.getEmail().equals(searchedEmail))
            {
                return currentUser;
            }
        }
        return null;
    }

    boolean isEmailUse(String searchedEmail)
    {
        return !(getUserByEmail(searchedEmail) == null);
    }

    List<UserTmp> getUsersByName(String searchedName)
    {
        UserTmp currentUser;
        List<UserTmp> usersWithSearchedName = new ArrayList<UserTmp>();
        for (int i = 0; i < usersList.size(); i++)
        {
            currentUser = usersList.get(i);
            if (currentUser.getName().equals(searchedName))
            {
                usersWithSearchedName.add(currentUser);
            }
        }
        return usersWithSearchedName;
    }

    List<UserTmp> getUsersBySurname(String searchedSurname)
    {
        UserTmp currentUser;
        List<UserTmp> usersWithSearchedSurname = new ArrayList<UserTmp>();
        for (int i = 0; i < usersList.size(); i++)
        {
            currentUser = usersList.get(i);
            if (currentUser.getSurname().equals(searchedSurname))
            {
                usersWithSearchedSurname.add(currentUser);
            }
        }
        return usersWithSearchedSurname;
    }

    List<UserTmp> getUsersByFullname(String searchedName, String searchedSurname)
    {
        UserTmp currentUser;
        List<UserTmp> usersWithSearchedFullname = new ArrayList<UserTmp>();
        for (int i = 0; i < usersList.size(); i++)
        {
            currentUser = usersList.get(i);
            if (currentUser.getName().equals(searchedName) && currentUser.getSurname().equals(searchedSurname))
            {
                usersWithSearchedFullname.add(currentUser);
            }
        }
        return usersWithSearchedFullname;
    }

    List<UserTmp> getUsersByFullname(String searchedFullname)
    {
        String name;
        String surname;
        String[] fullName = searchedFullname.split(" ");
        name = fullName[0];
        surname = fullName[1];

        return getUsersByFullname(name, surname);
    }

    String getRandomIdString()
    {
        Random randomizer = new Random();
        StringBuilder outputString = new StringBuilder();
        int[] randomId = new int[5];


        for (int i = 0; i < randomId.length; i++)
        {
            randomId[i] = randomizer.nextInt(10);
            outputString.append(randomId[i]);
        }

        return outputString.toString();

    }

    String[] getUserData(UserTmp user)
    {
        //TODO: question - co, jeśli ilość danych w klasie User zostanie zwiększona z 5 do 6? Out of bounds error?
        String[] outputData = new String[5];

        outputData[0] = user.getId();
        outputData[1] = user.getName();
        outputData[2] = user.getSurname();
        outputData[3] = user.getEmail();
        outputData[4] = user.getPassword();

        return outputData;
    }

    void addUser(String name, String surname, String email, String password) throws IOException
    {
        String newId = getRandomIdString();

        while (isIdInUse(newId))
        {
            newId = getRandomIdString();
        }

        if (isEmailUse(email))
        {
            System.out.println("E-mail address: <" + email + "> is already taken. New user can't be added.");
            return;
        }

        UserTmp newUser = new UserTmp(newId, name, surname, email, password);
        usersList.add(newUser);

        CSVwriter csv = new CSVwriter(databaseName);
        csv.addElementToDatabase("User", getUserData(newUser));

    }

    void deleteUser(UserTmp userToDelete) throws IOException
    {
        //delete from list
        for (Iterator<UserTmp> iter = this.usersList.listIterator(); iter.hasNext(); )
        {
            UserTmp currentUser = iter.next();
            if (currentUser.equals(userToDelete))
            {
                iter.remove();
            }
        }

        //delete from database
        CSVwriter csv = new CSVwriter(this.databaseName);

        csv.deleteElement("User", this.getUserData(userToDelete));

    }

    void editUser(UserTmp userToEdit, String[] newUserData) throws IOException
    {
        //delete from list
        UserTmp currentUser = new UserTmp();

        for (Iterator<UserTmp> iter = this.usersList.listIterator(); iter.hasNext(); )
        {
            UserTmp iterUser = iter.next();
            if (iterUser.equals(userToEdit))
            {
                currentUser = iterUser;
                break;
            }

        }

        String[] currentUserData = getUserData(currentUser);

        for (int i = 0; i < currentUserData.length; i++)
        {
            if (newUserData[i].equals(""))
            {
                newUserData[i] = currentUserData[i];
            }
        }


        //edit in database
        CSVwriter csv = new CSVwriter(this.databaseName);
        csv.editElement("User", this.getUserData(userToEdit), newUserData);

        //TODO: jak to zoptymalizować, żeby nie trzeba było tak listować pól klasy User?
        currentUser.setId(newUserData[0]);
        currentUser.setName(newUserData[1]);
        currentUser.setSurname(newUserData[2]);
        currentUser.setEmail(newUserData[3]);
        currentUser.setPassword(newUserData[4]);

    }


}
