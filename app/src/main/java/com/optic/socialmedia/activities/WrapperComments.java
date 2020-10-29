package com.optic.socialmedia.activities;

import com.optic.socialmedia.models.Comment;

import java.util.List;

public class WrapperComments {
    List<Comment> listaComentarios;
    List<String> misTextos;
    public List<Comment> getListaComentarios() {
        return listaComentarios;
    }

    public List<String> getMisTextos() {
        return misTextos;
    }

    public void setMisTextos(List<String> misTextos) {
        this.misTextos = misTextos;
    }

    public void setListaComentarios(List<Comment> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }
}
