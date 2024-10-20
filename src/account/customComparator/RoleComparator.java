package account.customComparator;

import java.util.Comparator;

/**
 * This is a custom comparator to compare the role name without the prefix of ROLE_
 */
public class RoleComparator implements Comparator<String> {

    @Override
    public int compare(String role1, String role2) {
        // Remove the "ROLE_" prefix before comparing
        // Use the replace method to remove the "ROLE_" prefix
        String strippedRole1 = role1.replace("ROLE_", "");
        String strippedRole2 = role2.replace("ROLE_", "");

        // Compare based on the stripped role names
        return strippedRole1.compareTo(strippedRole2);
    }
}
