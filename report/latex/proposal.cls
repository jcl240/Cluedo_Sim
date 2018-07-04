% ===================================================================
% (c) Karl Isaac 2007
% proposal Proposal Class for LaTeX
%
% ===================================================================

% Fundamental setup
\NeedsTeXFormat{LaTeX2e}
\ProvidesPackage{proposal}[2010/07/26 Karl Isaac Proposal Class]
\LoadClassWithOptions{article}


% Packages
\RequirePackage{listings}
\RequirePackage{charter}
\RequirePackage{fancyhdr}
\RequirePackage{color}
%\RequirePackage{a4wide}
\RequirePackage{graphicx}
\RequirePackage{fancybox}
%\RequirePackage{humanist}
\RequirePackage{fancyvrb}
\RequirePackage{rcs}
%\RequirePackage[hyperref=true,style=authoryear,autopunct=yes,sortcites=true,isbn=false,doi=false,url=false,eprint=false,natbib=true]{biblatex}
\RequirePackage{ifthen}
\RequirePackage{pdfpages}
%\RequirePackage[toc,page,title,titletoc,header,]{appendix}
\RequirePackage[page,title,titletoc,header,]{appendix}
\RequirePackage{booktabs}
\RequirePackage[font=sl,labelfont=bf]{caption}

\RequirePackage{url} 
\RequirePackage{enumitem} 
\RequirePackage{array, longtable} 
\RequirePackage{amsmath}
\RequirePackage{soul}

\RequirePackage{hyperref}
\RequirePackage{rotating}
\RequirePackage[numbers]{natbib}
% No hyperlink borders
\def\@pdfborder{0 0 0}%
\let\@pdfborderstyle\@empty

%TOC overfull boxes
\def\@pnumwidth{18.3pt}

% Colours
\definecolor{ysgreen}{rgb}{0.27450980392156865, 0.59607843137254901, 0.035294117647058823}
\definecolor{ysblue}{rgb}{0.262745098039216, 0.219607843137255, 0.662745098039216}

% Custom bullet points

%\newcommand{\submittedtext}{{This dissertation is submitted in partial fulfilment of the requirements for the\\\vspace*{1ex}\emph{MSc Mobile Computing}\\\vspace*{1ex}at the\\\vspace*{1ex}University of Westminster}}
%
%
% DECLARATIONS
%
% These macros are used to declare arguments needed for the
% construction of the title page and other preamble.

% The student number
\def\studentnumber#1{\gdef\@studentnumber{#1}}

% The year and term the degree will be officially conferred
\def\degreedate#1{\gdef\@degreedate{#1}}
% The full (unabbreviated) name of the degree
\def\degree#1{\gdef\@degree{#1}}
% The name of your college or department(eg. Trinity, Pembroke, Maths, Physics)
\def\collegeordept#1{\gdef\@collegeordept{#1}}
% The name of your University
\def\university#1{\gdef\@university{#1}}
% Defining the crest
\def\crest#1{\gdef\@crest{#1}}

% These macros define an environment for front matter that is always
% single column even in a double-column document.

\newenvironment{alwayssingle}{%
       \@restonecolfalse\if@twocolumn\@restonecoltrue\onecolumn
       \else\newpage\fi}
       {\if@restonecol\twocolumn\else\newpage\fi}

%define title page layout
\renewcommand{\maketitle}{%
\begin{alwayssingle}
    \renewcommand{\footnotesize}{\small}
    \renewcommand{\footnoterule}{\relax}
    \thispagestyle{empty}
%  \null\vfill
  \begin{center}
    { \Huge {\bfseries {\@title}} \par}
 {\large \vspace*{35mm} {{\@crest} \par} \vspace*{25mm}}
%%    {\large \vspace*{85mm}  \vspace*{25mm}}
    {{\huge \@author} \par}
    \vspace*{1ex}
    {{\Large \@studentnumber} \par}
    
{\large \vspace*{3ex}
    {{\@collegeordept} \par}
\vspace*{1ex}
    {{\@university} \par}
\vspace*{20mm}
    { \par}
\vspace*{1ex}
    {\it {\@degree} \par}
\vspace*{2ex}
    {\@degreedate}}
  \end{center}
  \null\vfill
\end{alwayssingle}}


% Custom paragraph
\newcommand{\mypar}{\paragraph{}\par}


% Listings settings
% For lstlisting environment or lstinputlisting
\lstset{
  basicstyle=\footnotesize,
  breaklines=true,
  keywordstyle=\color{blue},
  stringstyle=\ttfamily,
  numbers=left,
  numberstyle=\tiny\color{red},
}

