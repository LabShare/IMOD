/*  IMOD VERSION 2.7.9
 *
 *  imodv_views.cpp -- Edit, store, and access view list in model
 *                     Companion form class is imodvViewsForm in 
 *                     formv_views.ui[.h]
 *
 *  Original author: James Kremer
 *  Revised by: David Mastronarde   email: mast@colorado.edu
 */

/*****************************************************************************
 *   Copyright (C) 1995-2002 by Boulder Laboratory for 3-Dimensional Fine    *
 *   Structure ("BL3DFS") and the Regents of the University of Colorado.     *
 *                                                                           *
 *   BL3DFS reserves the exclusive rights of preparing derivative works,     *
 *   distributing copies for sale, lease or lending and displaying this      *
 *   software and documentation.                                             *
 *   Users may reproduce the software and documentation as long as the       *
 *   copyright notice and other notices are preserved.                       *
 *   Neither the software nor the documentation may be distributed for       *
 *   profit, either in original form or in derivative works.                 *
 *                                                                           *
 *   THIS SOFTWARE AND/OR DOCUMENTATION IS PROVIDED WITH NO WARRANTY,        *
 *   EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTY OF          *
 *   MERCHANTABILITY AND WARRANTY OF FITNESS FOR A PARTICULAR PURPOSE.       *
 *                                                                           *
 *   This work is supported by NIH biotechnology grant #RR00592,             *
 *   for the Boulder Laboratory for 3-Dimensional Fine Structure.            *
 *   University of Colorado, MCDB Box 347, Boulder, CO 80309                 *
 *****************************************************************************/
/*  $Author$

    $Date$

    $Revision$

    $Log$
    Revision 4.1  2003/02/10 20:29:02  mast
    autox.cpp

    Revision 1.1.2.8  2003/01/18 01:10:17  mast
    add include of dia_qtutils

    Revision 1.1.2.7  2003/01/13 07:21:38  mast
    Changes to use new dialog manager class

    Revision 1.1.2.6  2002/12/30 06:40:24  mast
    Prevent multiple draws, adapt to dialog-widget control

    Revision 1.1.2.5  2002/12/27 17:45:01  mast
    clean up unused variable

*/

#include <stdio.h>
#include <string.h>
#include "formv_views.h"
#include "imodv.h"
#include "dia_qtutils.h"
#include "imod.h"
#include "imod_input.h"
#include "imodv_gfx.h"
#include "imodv_input.h"
#include "imodv_menu.h"
#include "imodv_control.h"
#include "imodv_light.h"
#include "imodv_views.h"
#include "imodv_depthcue.h"
#include "imodv_objed.h"
#include "control.h"


struct imodv_viewed
{
  imodvViewsForm *dia;
  ImodvApp  *a;
};

static struct imodv_viewed viewStruct;
static struct imodv_viewed *ved = &viewStruct;

static void build_list(ImodvApp *a);
static void imodvUpdateView(ImodvApp *a);
static void manage_world_flags(ImodvApp *a, Iview *view);

static int auto_store = 1;

static void imodvUpdateView(ImodvApp *a)
{
  imodvControlSetView(a);
  imodvObjedNewView();
  imodvSetLight(a->imod->view);
  imodvMenuLight(a->imod->view->world & VIEW_WORLD_LIGHT);

  if (a->imod->view->world & VIEW_WORLD_WIREFRAME)
    a->wireframe = 1;    
  else
    a->wireframe = 0;
  imodvMenuWireframe(a->imod->view->world & VIEW_WORLD_WIREFRAME);

  if (a->imod->view->world & VIEW_WORLD_LOWRES)
    a->lowres = 1;    
  else
    a->lowres = 0;
  imodvMenuLowres(a->imod->view->world & VIEW_WORLD_LOWRES);
  imodvDepthCueSetWidgets();
}

/* Update when a different model is being displayed */
void imodvUpdateModel(ImodvApp *a)
{
  imodvUpdateView(a);
  if (!ved->dia)
    return;
  ved->dia->removeAllItems();
  build_list(a);
  imodvViewsGoto(a->imod->cview, false);
  ved->dia->selectItem(a->imod->cview, true);
}

/* set the current application flags into the current view */
static void manage_world_flags(ImodvApp *a, Iview *view)
{
  if (a->lighting)
    view->world |= VIEW_WORLD_LIGHT;
  else
    view->world &= ~VIEW_WORLD_LIGHT;
  if (a->wireframe)
    view->world |= VIEW_WORLD_WIREFRAME;
  else
    view->world &= ~VIEW_WORLD_WIREFRAME;
  if (a->lowres)
    view->world |= VIEW_WORLD_LOWRES;
  else
    view->world &= ~VIEW_WORLD_LOWRES;
}

/* Automatically store the current view if auto_store is set */
void imodvAutoStoreView(ImodvApp *a)
{
  if (!auto_store || !a->imod->cview)
    return;
  manage_world_flags(a, a->imod->view);
  imodViewStore(a->imod, a->imod->cview);
}

void imodvViewsHelp()
{
  dia_vasmsg
    ("View Edit Dialog Help.\n\n",
     "\tWhen you store a view, you save the orientation, size, and "
     "lighting conditions of the whole model, and also the color,"
     " display type, material, and other properties of all of the "
     "objects.\n\n"
     "\tClick a view in the list to select and"
     " display it.  "
     "The default view can be seen by "
     "clicking the Default View at the top of the list.\n",
     "\tThe [Store] button stores the properties of the current display"
     " in the currently selected view.\n"
     "\tThe [Revert] button returns the display to the stored values of"
     " the currently selected view.\n"
     "\tThe [Delete] button deletes the currently selected view.\n"
     "\tThe [New View] button adds a new view to the list, with the "
     "properties of the current display.\n",
     "\tThe [Save Model] button will save the model to a file and make "
     "the existing file be a backup with extension ~.\n"
     "\tThe [Autostore] toggle button controls whether view changes are "
     "automatically saved for you.  If this button is selected, the "
     "current display properties are stored into the current view "
     "whenever you go to a different view and whenever you save the "
     "model.  In other words, what you see is what you save, without "
     "your having to push the [Store] button.  To return to the stored "
     "settings of a view when operating in this mode, press the [Revert]"
     "button or double-click on the view in the list "
     "before going on to a different view or saving the model.  If "
     "[Autostore] is not selected, your display changes are not saved "
     "into the current view unless you push [Store]."
     "\n\n"
     "\tYou can edit the name of the currently selected view in the "
     "edit box at the bottom.  Press Enter after changing a name.\n\n"
     "\tThe Default View is set when the Model View window is opened"
     " and cannot be changed.  It will cease to exist when the Model"
     " View window is closed.  If this view is precious to you, you"
     " should either store it as a separate view, or redisplay it before"
     " exiting Model View.\n\n",
     "\tHotkeys: The regular up and down arrow keys (not the ones in the "
     "numeric keypad) can be used go up or down by one view, and PageUp and "
     "PageDown can be used to go up or down in the list by many views.  Escape"
     " will close the dialog box, and "
     "other keys are passed on to the model display window.",
     NULL);
  return;
}

/* Return to default view */
void imodvViewsDefault(bool draw)
{
  if (!ved->a->imod) return;

  /* Store the current view before changing to default */
  imodvAutoStoreView(ved->a);
     
  ved->a->imod->cview = 0;
  imodViewModelDefault(ved->a->imod, ved->a->imod->view);
  imodViewUse(ved->a->imod);
  imodvDrawImodImages();
  imodvUpdateView(ved->a);
  if (draw)
    imodvDraw(ved->a);
}

// Done: tell the box to close
void imodvViewsDone()
{
  ved->dia->close();
}
  
// Closing - do we need to autostore view?
void imodvViewsClosing()
{
  imodvDialogManager.remove((QWidget *)ved->dia);
  ved->dia = NULL;
}

// Open, close, or raise the dialog box
void imodvViewEditDialog(ImodvApp *a, int state)
{
  QString qstr;
  char *window_name;
  static int first = 1;

  if (first){
    ved->dia = NULL;
    first = 0;
  }
  if (!state){
    if (ved->dia)
      ved->dia->close();
    return;
  }
  if (ved->dia){
    ved->dia->raise();
    return;
  }
  ved->a = a;

  ved->dia = new imodvViewsForm(NULL, NULL, //false,
                                Qt::WDestructiveClose | Qt::WType_TopLevel);

  // Set title bar
  window_name = imodwEithername("Imodv Views: ", a->imod->fileName, 1);
  qstr = window_name;
  if (window_name)
    free(window_name);
  if (!qstr.isEmpty())
    ved->dia->setCaption(qstr);

  build_list(a);
  ved->dia->setAutostore(auto_store);
  ved->dia->selectItem(a->imod->cview, true);
  imodvDialogManager.add((QWidget *)ved->dia, IMODV_DIALOG);
  ved->dia->show();
}

// Save model: call appropriate routine depending on whether imod or standalone
void imodvViewsSave()
{
  if (ved->a->standalone)
    imodvFileSave();
  else
    inputSaveModel(App->cvi);
}

/*
 * The goto view callback.
 */
void imodvViewsGoto(int item, bool draw)
{
   if (!ved->a->imod)
     return;

  /* If changing views, store the current view before changing */
  if (item != ved->a->imod->cview)
    imodvAutoStoreView(ved->a);

  ved->a->imod->cview = item;

  if (item == 0) {
    imodvViewsDefault(draw);
    return;
  }
     
  imodViewUse(ved->a->imod);
  imodvDrawImodImages();

  imodvUpdateView(ved->a);
  if (draw)
    imodvDraw(Imodv);
}


/* 
 * The store view callback.
 * Sets the view in the window to the current view. 
 */
void imodvViewsStore(int item)
{
  Iview *view = ved->a->imod->view;

  /* If changing views, store the current view before changing */
  if (item != ved->a->imod->cview)
    imodvAutoStoreView(ved->a);

  ved->a->imod->cview = item;
     
  manage_world_flags(ved->a, view);

  imodViewStore(ved->a->imod, item);
}

/* Make a new view, the form already found a unique label  */
void imodvViewsNew(const char *label)
{
  int cview;
  Iview *view = ved->a->imod->view;

  if (!ved->a->imod) return;

  imodvAutoStoreView(ved->a);

  cview = ved->a->imod->cview;
  imodViewModelNew(ved->a->imod);
  view = ved->a->imod->view;
  ved->a->imod->cview = ved->a->imod->viewsize - 1;
    
  if (cview == ved->a->imod->cview) {
    fprintf(stderr,"imodv: create view error\n");
    return; /* no view created. */
  }

  manage_world_flags(ved->a, view);

  cview = ved->a->imod->cview;

  strcpy(ved->a->imod->view[cview].label, label);
  imodViewStore(ved->a->imod, cview);

  ved->dia->addItem(ved->a->imod->view[cview].label);
  ved->dia->selectItem(cview, true);
}

/* Delete a view */
void imodvViewsDelete(int item, int newCurrent)
{
  Imod *imod = ved->a->imod;
  int i;

  if (!imod || item <= 0 || imod->viewsize < 2)
    return;

  // How about deleting the existing view?  No need, if the array grows
  // again the space is reused
  for(i = item; i < imod->viewsize; i++){
    imod->view[i] = imod->view[i+1];
  }

  imod->viewsize--;
  imod->cview = newCurrent >= 0 ? newCurrent : 0;
  imodvViewsGoto(imod->cview, true);
  ved->dia->selectItem(imod->cview, true);
}

// A new label has been entered
void imodvViewsLabel(const char *label, int item)
{
  strcpy( ved->a->imod->view[item].label, label);
}

void imodvViewsAutostore(int state)
{
  auto_store = state;
}

// send the list of views to the form
static void build_list(ImodvApp *a)
{
  int i;

  sprintf(a->imod->view->label, "Original Default View");
  for(i = 0; i < a->imod->viewsize; i++)
    ved->dia->addItem(a->imod->view[i].label);
}
