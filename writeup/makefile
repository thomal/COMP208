pdf : project.tex text/*/*.tex
	pdflatex project.tex
	pdflatex project.tex # because of the TOC and todo list
	bibtex project.aux
	pdflatex project.tex # because of latexbib
	pdflatex project.tex # '
clean : project.log project.aux project.toc project.tdo project.bbl project.blg project-blx.bib
	rm project.log
	rm project.aux
	rm project.toc
	rm project.tdo
	rm project.bbl
	rm project.blg
	rm project-blx.bib
