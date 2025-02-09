#!/bin/bash

# --- Configuration ---
DEV_DIR="/home/manyaraisinghani/spring-petclinic"              # Directory where your development changes are
EC2_HOST="35.208.177.203"   # External IP of your EC2 instance - Deploy Server
EC2_INSTANCE="manya-project-instance" # External EC2 Instance Name - Deploy Server Name
EC2_INSTANCE_ZONE="us-central1-c" # External EC2 Instance Name - Deploy Server Zone
REMOTE_BUILD_SCRIPT="/home/manyaraisinghani/health-prediction/start_health_app.sh" # Path to build/deploy script on EC2
REMOTE_LOG_FILE="/home/manyaraisinghani/health-prediction/logs/petstore.log"      # Path to log file on EC2
REMOTE_REPO_DIR="/home/manyaraisinghani/health-prediction" # Path to the repository directory on EC2
PROJECT_NAME="my-genome-project-p2"


# --- Script Start ---
echo "Starting deployment process..."

# --- 1. Commit changes in the dev directory ---
echo "Committing changes in $DEV_DIR directory..."
cd "$DEV_DIR" || { echo "Error: Could not change directory to $DEV_DIR"; exit 1; }
git add .
git commit -m "Automated deployment commit from deploy.sh"
if [ $? -ne 0 ]; then
  echo "Error: Git commit failed. Please check manually."
  exit 1
fi
echo "Changes committed successfully."
#cd .. # Go back to the script's original directory

# 2. Push changes to Git 
echo "Pushing changes to Git..."
echo "Current directory: $(pwd)"
git push origin main  # Replace 'main' with your branch name if different
if [ $? -ne 0 ]; then
   echo "Error: Git push failed. Please check manually and ensure your branch is correct."
   exit 1
fi
echo "Changes pushed to Git. "

#2.1 Set Project to my project
echo "Setting project to ($PROJECT_NAME)..."
gcloud config set project $PROJECT_NAME
echo "Project set."

# --- 3. Trigger build and deploy on EC2 instance ---
echo "Triggering build and deploy on EC2 instance ($EC2_INSTANCE)..."
echo "Connecting to the EC2 instance ($EC2_INSTANCE)..."
gcloud compute ssh $EC2_INSTANCE --zone=$EC2_INSTANCE_ZONE << EOF

    echo "--- Starting commands on EC2 ---" # Keep this line for now
    whoami                                    # <--- SIMPLIFIED: Just run 'whoami' 
    pwd
    cd ${REMOTE_REPO_DIR}
    git pull origin main
    pwd
    echo "--- Commands on EC2 completed ---"   # Keep this line for now
EOF


#   echo "--- Starting commands on EC2 ---"
#   cd ${REMOTE_REPO_DIR}
#   echo "Current directory: $(pwd)"
#   git pull origin main || { echo "Error: Git pull failed on EC2. Check repository and permissions."; exit 1; } # Pull latest code
#   echo "Git pull successful."
#   echo "${REMOTE_BUILD_SCRIPT} script executing on EC2."
#   bash ${REMOTE_BUILD_SCRIPT} || { echo "Error: Build and deploy script failed on EC2. Check logs in $REMOTE_REPO_DIR."; exit 1; } # Execute build/deploy script
#   echo "Deploy script executed. Checking logs next..."
# EOF

if [ $? -ne 0 ]; then
  echo "Error: SSH command execution failed. Check SSH connection and EC2 instance."
  exit 1
fi

# --- 4. Tail logs from EC2 instance ---
echo "Tailing logs from EC2 instance in the background..."
# tail -f $REMOTE_LOG_FILE

echo "Deployment process started. Check logs for details."

exit 0
