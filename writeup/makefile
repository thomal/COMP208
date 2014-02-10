pdf : project.tex text/*/*.tex
	pdflatex project.tex
	pdflatex project.tex # because of the TOC and todo list
	bibtex project.aux
	pdflatex project.tex # because of BibTeX
	pdflatex project.tex # '
clean : project.log project.aux project.toc project.tdo project.bbl project.blg
	rm project.log
	rm project.aux
	rm project.toc
	rm project.tdo
	rm project.bbl
	rm project.blg
