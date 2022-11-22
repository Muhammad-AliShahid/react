package be.rubus.workshop.security.challenge2.model;

import javax.persistence.*;

@Entity
@Table(name = "user_groups")
public class UserGroup {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name", length = 64)
    private String userName;

    @Column(name = "group_name", length = 64)
    private String groupName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGroup)) {
            return false;
        }

        UserGroup userGroup = (UserGroup) o;

        if (!userName.equals(userGroup.userName)) {
            return false;
        }
        return groupName.equals(userGroup.groupName);
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + groupName.hashCode();
        return result;
    }
}
