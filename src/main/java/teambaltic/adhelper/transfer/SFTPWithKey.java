/**
 * SFTPWithKey.java
 *
 * Created on 22.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.transfer;


import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import com.jcraft.jsch.UserInfo;


//############################################################################
public class SFTPWithKey
{

    public static void main(final String[] args) {
        downloadFile();
    }


    private static boolean downloadFile(){

        final String host = "diskstation";
        final String user = "Test";
        final String password = "";
        final String fileName = "Test-Daten/Anmerkungen.txt";
        final String localFilePath = "FTPDownload/Anmerkungen-downloaded.txt";

        // without passphrase
        final String keyPath = "./Daten/Einstellungen/id_rsa";
        final String passphrase = null;

        final StandardFileSystemManager sysManager = new StandardFileSystemManager();

        //download der Datei
        try {
            sysManager.init();

            final FileObject localFile = sysManager.resolveFile(new File(localFilePath).getAbsolutePath());

            final String aConnectionString = createConnectionString(host, user, password, keyPath, passphrase, fileName);
            final FileSystemOptions aDefaultOptions = createDefaultOptions(keyPath, passphrase);
            final FileObject remoteFile = sysManager.resolveFile(
                    aConnectionString, aDefaultOptions);

            //Selectors.SELECT_FILES --> A FileSelector that selects only the base file/folder.
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);


        } catch (final Exception e) {
            System.out.println("Downloading file failed: " + e.toString());
            return false;
        }finally{
            sysManager.close();
        }
        return true;
    }


    public static String createConnectionString(final String hostName, final String username, final String password, final String keyPath, final String passphrase, final String remoteFilePath) {

        if (keyPath != null) {
            return "sftp://" + username + "@" + hostName + "/" + remoteFilePath;
        } else {
            return "sftp://" + username + ":" + password + "@" + hostName + "/" + remoteFilePath;
        }
    }



    private static FileSystemOptions createDefaultOptions(final String keyPath, final String passphrase) throws FileSystemException{

        //create options for sftp
        final FileSystemOptions options = new FileSystemOptions();
        //ssh key
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        //set root directory to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, true);
        //timeout
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);

        if (keyPath != null) {
            final SftpPassphraseUserInfo aPassPhraseInfo = new SftpPassphraseUserInfo(passphrase);
            SftpFileSystemConfigBuilder.getInstance().setUserInfo(options, aPassPhraseInfo);
            SftpFileSystemConfigBuilder.getInstance().setIdentities(options, new File[] { new File(keyPath) });
        }


        return options;
    }



    public static class SftpPassphraseUserInfo implements UserInfo {

        private String passphrase = null;

        public SftpPassphraseUserInfo(final String pp) {
            passphrase = pp;
        }

        @Override
        public String getPassphrase() {
            return passphrase;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassphrase(final String arg0) {
            return true;
        }

        @Override
        public boolean promptPassword(final String arg0) {
            return false;
        }

        @Override
        public void showMessage(final String message) {

        }

        @Override
        public boolean promptYesNo(final String str) {
            return true;
        }

    }


}
// ############################################################################
