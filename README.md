# GitHub Multibranch Description Setter

New Jenkins jobs that are automatically created by the Multibranch Pipeline
plugin — e.g. when new Pull Requests are created on GitHub — only display the PR
number, and provide no further information.

This very basic plugin sets a description on such jobs, including the title of
the PR, and a link to its page on GitHub.

No configuration required: it will work with GitHub.com and GitHub Enterprise.
