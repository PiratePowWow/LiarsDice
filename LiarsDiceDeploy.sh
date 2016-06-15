echo '***************************************************'
echo '**** starting Liars-Dice deploy... ****'
echo '***************************************************'
echo '***************************************************'
echo '**** stopping Liars-Dice application... ****'
echo '***************************************************'
sudo service liars-dice stop
echo '***************************************************'
echo '**** pulling changes from git... ****'
echo '***************************************************'
cd /Users/PiratePowWow/IdeaProjects/Liars-Dice
git pull origin master
echo '***************************************************'
echo '**** deleting old Liars-Dice war... ****'
echo '***************************************************'
cd /Users/PiratePowWow/IdeaProjects/Liars-Dice/build/libs
rm *.war
echo '***************************************************'
echo '**** building Liars-Dice war... ****'
echo '***************************************************'
cd /Users/PiratePowWow/IdeaProjects/Liars-Dice
gradle build
echo '***************************************************'
echo '**** starting Liars-Dice application... ****'
echo '***************************************************'
sudo service liars-dice start
echo '***************************************************'
echo '**** Liars-Dice Deployed ****'
echo '***************************************************'