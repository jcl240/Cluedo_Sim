#!/bin/sh
# Grid Engine options (lines prefixed with #$)
#$ -N cluedo              
#$ -cwd                  
#$ -l h_vmem=8G
# -l h_rt=200:00:00
#$ -pe sharedmem 4
#$ -R y
#$ -o logs
#$ -e logs
#  These options are:
#  job name: -N
#  use the current working directory: -cwd
#  runtime limit of 5 minutes: -l h_rt
#  memory limit of 1 Gbyte: -l h_vmem
# Initialise the environment modules
. /etc/profile.d/modules.sh
 
# Load Java
module load java/jdk/1.8.0
# module load intel 

# Run the program
java -jar Cluedo_Sim/out/artifacts/Simulator/SCRIPTNAMEGOESHERE.jar
