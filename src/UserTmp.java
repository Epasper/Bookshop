public class UserTmp
{
    String id;
    String name;
    String surname;
    String email;
    String password;

    public UserTmp(String id, String name, String surname, String email, String password)
    {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public UserTmp(String[] userData)
    {
        this.id = userData[0];
        this.name = userData[1];
        this.surname = userData[2];
        this.email = userData[3];
        this.password = userData[4];
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
