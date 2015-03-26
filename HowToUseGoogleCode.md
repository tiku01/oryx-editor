# Introduction #

This document describes how to use the Google Code page for the Oryx project. It explains how to create new issues for bugs or new features and how to track your work with the issues list. Furthermore, rules for the subversion repository are introduced.


# Documents #

All public documents should be made available in the wiki or in the 'Downloads' section in case of PDF files, for example. All private documents should be added to the ['Files' section of our B3MN Google Group](http://groups.google.de/group/b3mn/files). The group is closed so that the files are only available for team members.


# Issues #

In the issues list all bugs, enhancements, documentation tasks and other tasks must be documented and tracked. However, those different issue types are handled differently. So, I will explain each type separately.

All new issues are discussed in the weekly team meeting to adjust the priority, if necessary.

## Bugs/Defects ##

Bugs in the issue list are all issues with a _Type-Defect_ label. Defects should be added to the list as soon as they are found. The defect's description must comply to the description's template:
```
What steps will reproduce the problem?
1. 
2. 
3. 

What is the expected output? 


What do you see instead?


Please use labels and text to provide additional information.
```
You must describe the steps to reproduce the problem as detailed as possible. In field **Status** set the status to _New_. Delete the name in field **Owner**, because as long as no one is fixing the issue, it is not assigned to any person. If you want to get notified, when someone is adding a comment, add your name in field **Cc**. In the **Labels** fields add label _Type-Defect_ and a priority.

The person responsible for bug verification has to reproduce the bug according to the steps described in the bug's description. If reproduction is successful, he has to set the bug's **Status** to _Accepted_ and check the priority.

The developers in the team have to check regularly, if there are any new bugs in their respective responsibility. Of course, bugs with a higher priority have to be fixed before bugs with a lower one. If a developer starts working on a bug fix, he has to enter his name in field **Owner** and set the **Status** to _Started_.

As soon as the developer has fixed the bug, he has to set the issue's **Status** to _Fixed_ and enter the SVN commit number in the issue's comments. However, this status does not close the bug. The tester has to check, if the bug is really fixed. If he can approve the fix, he has to set the issue's **Status** to _Verified_. This closes the issue.

## Enhancements ##

If one has an idea for an enhancement, he can add it to the issues list. A detailed description of the enhancement must be given. Furthermore, the field **Status** must be set to _New_, the field **Owner** has to be empty and the label _Type-Defect_ has to be replaced with _Type-Enhancement_.

New enhancements are discussed in the weekly team meeting and if accepted, the project manager has to set the **Status** to _Accepted_.

If a developer starts working on an enhancement, he has to enter his name in field **Owner** and set the **Status** to _Started_.

As soon as the developer has implemented the enhancement, he has to set the issue's **Status** to _Implemented_ and enter the SVN commit number in the issue's comments. However, this status does not close the issue. The tester has to check, if the bug is really fixed. If he can approve the fix, he has to set the issue's **Status** to _Verified_. This closes the issue.

## Documentation Tasks / Other Tasks ##

Documentation and other tasks has to be tracked accordingly. The **Type** label has to be set to _Type-Documentation_ or _Type-Task_ respectively. The only difference is that the result usually is not committed to the repository. Documents must be added to the wiki or downloads section (or to the Google Group, if the document is private). The test reader has to verify the written documents.

# SVN Commits #

Each commit has to contain a detailed description what is changed in that commit. If the commit is related to an issue, the issue's number has to be added to the description. Do not commit several work packages at once!