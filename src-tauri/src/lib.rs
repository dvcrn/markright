#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
  use tauri::Emitter;
  tauri::Builder::default()
    .plugin(tauri_plugin_opener::init())
    .plugin(tauri_plugin_fs::init())
    .plugin(tauri_plugin_dialog::init())
    .setup(|app| {
      if cfg!(debug_assertions) {
        app.handle().plugin(
          tauri_plugin_log::Builder::default()
            .level(log::LevelFilter::Info)
            .build(),
        )?;
      }
      
      use tauri::menu::{Menu, MenuItem, Submenu, PredefinedMenuItem};
      let handle = app.handle();
      
      let file_menu = Submenu::with_items(
          handle,
          "File",
          true,
          &[
              &MenuItem::with_id(handle, "open", "Open...", true, Some("CmdOrCtrl+O"))?,
              &MenuItem::with_id(handle, "save", "Save", true, Some("CmdOrCtrl+S"))?,
              &MenuItem::with_id(handle, "save_as", "Save As...", true, Some("CmdOrCtrl+Shift+S"))?,
              &MenuItem::with_id(handle, "quit", "Quit", true, Some("CmdOrCtrl+Q"))?,
          ],
      )?;

      let edit_menu = Submenu::with_items(
          handle,
          "Edit",
          true,
          &[
              &PredefinedMenuItem::undo(handle, Some("Undo"))?,
              &PredefinedMenuItem::redo(handle, Some("Redo"))?,
              &PredefinedMenuItem::cut(handle, Some("Cut"))?,
              &PredefinedMenuItem::copy(handle, Some("Copy"))?,
              &PredefinedMenuItem::paste(handle, Some("Paste"))?,
              &PredefinedMenuItem::select_all(handle, Some("Select All"))?,
          ],
      )?;

      let menu = Menu::with_items(handle, &[&file_menu, &edit_menu])?;
      app.set_menu(menu)?;

      Ok(())
    })
    .on_menu_event(|app, event| {
        let id = event.id().as_ref();
        match id {
            "quit" => {
                app.exit(0);
            }
            "open" | "save" | "save_as" => {
                // Emit event to frontend
                let _ = app.emit(format!("menu-{}", id).as_str(), ());
            }
            // Edit commands are usually handled natively by the OS/Webview if roles are set,
            // but since we used custom items, we might need to handle them or use PredefinedMenuItems.
            // For now, let's see if we can use PredefinedMenuItems for Edit.
            _ => {}
        }
    })
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}
