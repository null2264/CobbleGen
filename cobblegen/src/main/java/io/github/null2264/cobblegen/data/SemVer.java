package io.github.null2264.cobblegen.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to represent and compare Semantic Versioning (SemVer) strings.
 * This class implements the Comparable interface to allow for easy sorting
 * and comparison of version objects.
 *
 * @see <a href="https://semver.org/">https://semver.org/</a>
 */
public final class SemVer implements Comparable<SemVer> {

    private final int major;
    private final int minor;
    private final int patch;
    private final String preRelease;
    private final String buildMetadata;

    /**
     * Constructs a SemVer object by parsing a version string.
     *
     * @param version The version string to parse.
     * @throws IllegalArgumentException if the version string is not a valid SemVer string.
     */
    public SemVer(String version) {
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty.");
        }

        // Regex for parsing a SemVer string based on the official spec
        // https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
        Pattern pattern = Pattern.compile(
            "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");
        Matcher matcher = pattern.matcher(version);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid SemVer string: " + version);
        }

        this.major = Integer.parseInt(matcher.group(1));
        this.minor = Integer.parseInt(matcher.group(2));
        this.patch = Integer.parseInt(matcher.group(3));
        this.preRelease = matcher.group(4) != null ? matcher.group(4) : "";
        this.buildMetadata = matcher.group(5) != null ? matcher.group(5) : "";
    }

    /**
     * Compares this SemVer object with another.
     *
     * @param other The SemVer object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is older than, equal to, or newer than the specified object.
     */
    @Override
    public int compareTo(SemVer other) {
        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        if (this.patch != other.patch) {
            return Integer.compare(this.patch, other.patch);
        }

        // Compare pre-release versions
        boolean thisHasPreRelease = !this.preRelease.isEmpty();
        boolean otherHasPreRelease = !other.preRelease.isEmpty();

        if (thisHasPreRelease && !otherHasPreRelease) {
            return -1; // Pre-release versions have lower precedence (are older)
        }
        if (!thisHasPreRelease && otherHasPreRelease) {
            return 1;
        }
        if (thisHasPreRelease && otherHasPreRelease) {
            String[] thisPreReleaseParts = this.preRelease.split("\\.");
            String[] otherPreReleaseParts = other.preRelease.split("\\.");

            int minLength = Math.min(thisPreReleaseParts.length, otherPreReleaseParts.length);
            for (int i = 0; i < minLength; i++) {
                String thisPart = thisPreReleaseParts[i];
                String otherPart = otherPreReleaseParts[i];

                if (isNumeric(thisPart) && isNumeric(otherPart)) {
                    int thisNum = Integer.parseInt(thisPart);
                    int otherNum = Integer.parseInt(otherPart);
                    if (thisNum != otherNum) {
                        return Integer.compare(thisNum, otherNum);
                    }
                } else {
                    int lexicalComparison = thisPart.compareTo(otherPart);
                    if (lexicalComparison != 0) {
                        return lexicalComparison;
                    }
                }
            }

            return Integer.compare(thisPreReleaseParts.length, otherPreReleaseParts.length);
        }

        return 0; // Equal in terms of precedence
    }

    /**
     * Checks if a string is numeric.
     */
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    /**
     * Checks if this version is newer than another version.
     *
     * @param other The other version to compare to.
     * @return true if this version is newer than the other, false otherwise.
     */
    public boolean isNewerThan(SemVer other) {
        return this.compareTo(other) > 0;
    }

    /**
     * Checks if this version is newer than or equal to another version.
     *
     * @param other The other version to compare to.
     * @return true if this version is newer than or equal to the other, false otherwise.
     */
    public boolean isNewerThanOrEqualTo(SemVer other) {
        return this.compareTo(other) >= 0;
    }

    /**
     * Checks if this version is older than another version.
     *
     * @param other The other version to compare to.
     * @return true if this version is older than the other, false otherwise.
     */
    public boolean isOlderThan(SemVer other) {
        return this.compareTo(other) < 0;
    }

    /**
     * Checks if this version is older than or equal to another version.
     *
     * @param other The other version to compare to.
     * @return true if this version is older than or equal to the other, false otherwise.
     */
    public boolean isOlderThanOrEqualTo(SemVer other) {
        return this.compareTo(other) <= 0;
    }

    /**
     * Checks if this version is equal to another version in terms of precedence.
     * Note that build metadata is not considered in precedence.
     *
     * @param other The other version to compare to.
     * @return true if the versions are equal in precedence, false otherwise.
     */
    public boolean isEqualTo(SemVer other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemVer semVer = (SemVer) o;
        return major == semVer.major &&
            minor == semVer.minor &&
            patch == semVer.patch &&
            preRelease.equals(semVer.preRelease) &&
            buildMetadata.equals(semVer.buildMetadata);
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + preRelease.hashCode();
        result = 31 * result + buildMetadata.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append(".").append(minor).append(".").append(patch);
        if (!preRelease.isEmpty()) {
            sb.append("-").append(preRelease);
        }
        if (!buildMetadata.isEmpty()) {
            sb.append("+").append(buildMetadata);
        }
        return sb.toString();
    }

    /* #region Getters */
    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public int getPatch() { return patch; }
    public String getPreRelease() { return preRelease; }
    public String getBuildMetadata() { return buildMetadata; }
    /* #endregion */
}
