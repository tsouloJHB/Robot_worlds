# To Setup
## Locate 0021_robot_world/ServerProgram
    - ./runServer.sh (To compile and run the server program)
    - ./freezeCode.sh (To package and create a distributable server program. It will be stored in the target folder.)

## Run Server
    - Inside ServerProgram run ./StartServer

## Locate 0021_robot_world/CientProgram
    - ./runClient.sh (To compile and run run the client program)
    - ./freezeCode.sh (To package and creat a distributable client program. It will be stored in the target folder.)

## To run Client
    - Inside ClientProgram run ./StartGame

# Maven:
    - Locate ~/0021_robot_world/RobotWorlds-0021/
    - mvn clean (Clean package of from previous compilation)
    - mvn compile (Compile package)
    - mvn test (Run all tests)
    - mnv clean package (Do all of the above and get an executable out of it)

# Git Ignore the following files
    - Untrack the files first using this command 'git rm --cached specifyFileName'
    - Here are the files to untrack:
        - ClientProgram/RobotWorlds-0021/.RobotClient.json
        - runServer.sh
        - runClient.sh
    - Assuming all goes well the updated gitignore file will be able to sucessfully ignore these files when committing and pushing files
    NOTE: files will appear to be deleted when trying to commit this change but in reality they are no longer being tracked for changes.anges.
