package xyz.aoei.neovim

import java.io.{InputStream, OutputStream}

import scala.concurrent.Future

import xyz.aoei.msgpack.rpc.{Session, ExtendedType}

class Neovim(val in: InputStream, val out: OutputStream) extends NeovimBase(in, out) {
  override def types =
    List(ExtendedType(classOf[Buffer], 0, (buf: Buffer) => buf.data,
            (bytes: Array[Byte]) => new Buffer(session, bytes)),
        ExtendedType(classOf[Window], 1, (win: Window) => win.data,
            (bytes: Array[Byte]) => new Window(session, bytes)),
        ExtendedType(classOf[Tabpage], 2, (tab: Tabpage) => tab.data,
            (bytes: Array[Byte]) => new Tabpage(session, bytes)))
  def uiAttach(width: Int, height: Int, enable_rgb: Boolean): Unit =
    session.notify("ui_attach", width, height, enable_rgb)
  def uiDetach(): Unit = session.notify("ui_detach")
  def uiTryResize(width: Int, height: Int): Future[Object] = session.request[Object]("ui_try_resize", width, height)
  def command(str: String): Unit = session.notify("vim_command", str)
  def feedkeys(keys: String, mode: String, escape_csi: Boolean): Unit =
    session.notify("vim_feedkeys", keys, mode, escape_csi)
  def input(keys: String): Future[Int] = session.request[Int]("vim_input", keys)
  def replaceTermcodes(str: String, from_part: Boolean, do_lt: Boolean, special: Boolean): Future[String] =
    session.request[String]("vim_replace_termcodes", str, from_part, do_lt, special)
  def commandOutput(str: String): Future[String] = session.request[String]("vim_command_output", str)
  def eval(str: String): Future[Object] = session.request[Object]("vim_eval", str)
  def callFunction(fname: String, args: Array[Any]): Future[Object] =
    session.request[Object]("vim_call_function", fname, args)
  def strwidth(str: String): Future[Int] = session.request[Int]("vim_strwidth", str)
  def listRuntimePaths(): Future[List[String]] = session.request[List[String]]("vim_list_runtime_paths")
  def changeDirectory(dir: String): Unit = session.notify("vim_change_directory", dir)
  def getCurrentLine(): Future[String] = session.request[String]("vim_get_current_line")
  def setCurrentLine(line: String): Unit = session.notify("vim_set_current_line", line)
  def delCurrentLine(): Unit = session.notify("vim_del_current_line")
  def getVar(name: String): Future[Object] = session.request[Object]("vim_get_var", name)
  def setVar(name: String, value: Object): Future[Object] = session.request[Object]("vim_set_var", name, value)
  def delVar(name: String): Future[Object] = session.request[Object]("vim_del_var", name)
  def getVvar(name: String): Future[Object] = session.request[Object]("vim_get_vvar", name)
  def getOption(name: String): Future[Object] = session.request[Object]("vim_get_option", name)
  def setOption(name: String, value: Object): Unit = session.notify("vim_set_option", name, value)
  def outWrite(str: String): Unit = session.notify("vim_out_write", str)
  def errWrite(str: String): Unit = session.notify("vim_err_write", str)
  def reportError(str: String): Unit = session.notify("vim_report_error", str)
  def getBuffers(): Future[List[Buffer]] = session.request[List[Buffer]]("vim_get_buffers")
  def getCurrentBuffer(): Future[Buffer] = session.request[Buffer]("vim_get_current_buffer")
  def setCurrentBuffer(): Unit = session.notify("vim_set_current_buffer", this)
  def getWindows(): Future[List[Window]] = session.request[List[Window]]("vim_get_windows")
  def getCurrentWindow(): Future[Window] = session.request[Window]("vim_get_current_window")
  def setCurrentWindow(): Unit = session.notify("vim_set_current_window", this)
  def getTabpages(): Future[List[Tabpage]] = session.request[List[Tabpage]]("vim_get_tabpages")
  def getCurrentTabpage(): Future[Tabpage] = session.request[Tabpage]("vim_get_current_tabpage")
  def setCurrentTabpage(): Unit = session.notify("vim_set_current_tabpage", this)
  def subscribe(event: String): Unit = session.notify("vim_subscribe", event)
  def unsubscribe(event: String): Unit = session.notify("vim_unsubscribe", event)
  def nameToColor(name: String): Future[Int] = session.request[Int]("vim_name_to_color", name)
  def getColorMap(): Future[Map[String, Int]] = session.request[Map[String, Int]]("vim_get_color_map")
  def getApiInfo(): Future[Array[Any]] = session.request[Array[Any]]("vim_get_api_info")
}

class Buffer(val session: Session, val data: Array[Byte]) extends TypeBase {
  def lineCount(): Future[Int] = session.request[Int]("buffer_line_count", this)
  def getLine(index: Int): Future[String] = session.request[String]("buffer_get_line", this, index)
  def setLine(index: Int, line: String): Unit = session.notify("buffer_set_line", this, index, line)
  def delLine(index: Int): Unit = session.notify("buffer_del_line", this, index)
  def getLineSlice(start: Int, end: Int, include_start: Boolean, include_end: Boolean): Future[List[String]] =
    session.request[List[String]]("buffer_get_line_slice", this, start, end, include_start, include_end)
  def getLines(start: Int, end: Int, strict_indexing: Boolean): Future[List[String]] =
    session.request[List[String]]("buffer_get_lines", this, start, end, strict_indexing)
  def setLineSlice(start: Int, end: Int, include_start: Boolean, include_end: Boolean,
      replacement: List[String]): Unit =
    session.notify("buffer_set_line_slice", this, start, end, include_start, include_end, replacement)
  def setLines(start: Int, end: Int, strict_indexing: Boolean, replacement: List[String]): Unit =
    session.notify("buffer_set_lines", this, start, end, strict_indexing, replacement)
  def getVar(name: String): Future[Object] = session.request[Object]("buffer_get_var", this, name)
  def setVar(name: String, value: Object): Future[Object] =
    session.request[Object]("buffer_set_var", this, name, value)
  def delVar(name: String): Future[Object] = session.request[Object]("buffer_del_var", this, name)
  def getOption(name: String): Future[Object] = session.request[Object]("buffer_get_option", this, name)
  def setOption(name: String, value: Object): Unit = session.notify("buffer_set_option", this, name, value)
  def getNumber(): Future[Int] = session.request[Int]("buffer_get_number", this)
  def getName(): Future[String] = session.request[String]("buffer_get_name", this)
  def setName(name: String): Unit = session.notify("buffer_set_name", this, name)
  def isValid(): Future[Boolean] = session.request[Boolean]("buffer_is_valid", this)
  def insert(lnum: Int, lines: List[String]): Unit = session.notify("buffer_insert", this, lnum, lines)
  def getMark(name: String): Future[List[Int]] = session.request[List[Int]]("buffer_get_mark", this, name)
  def addHighlight(src_id: Int, hl_group: String, line: Int, col_start: Int, col_end: Int): Future[Int] =
    session.request[Int]("buffer_add_highlight", this, src_id, hl_group, line, col_start, col_end)
  def clearHighlight(src_id: Int, line_start: Int, line_end: Int): Unit =
    session.notify("buffer_clear_highlight", this, src_id, line_start, line_end)
}

class Window(val session: Session, val data: Array[Byte]) extends TypeBase {
  def getBuffer(): Future[Buffer] = session.request[Buffer]("window_get_buffer", this)
  def getCursor(): Future[List[Int]] = session.request[List[Int]]("window_get_cursor", this)
  def setCursor(pos: List[Int]): Unit = session.notify("window_set_cursor", this, pos)
  def getHeight(): Future[Int] = session.request[Int]("window_get_height", this)
  def setHeight(height: Int): Unit = session.notify("window_set_height", this, height)
  def getWidth(): Future[Int] = session.request[Int]("window_get_width", this)
  def setWidth(width: Int): Unit = session.notify("window_set_width", this, width)
  def getVar(name: String): Future[Object] = session.request[Object]("window_get_var", this, name)
  def setVar(name: String, value: Object): Future[Object] =
    session.request[Object]("window_set_var", this, name, value)
  def delVar(name: String): Future[Object] = session.request[Object]("window_del_var", this, name)
  def getOption(name: String): Future[Object] = session.request[Object]("window_get_option", this, name)
  def setOption(name: String, value: Object): Unit = session.notify("window_set_option", this, name, value)
  def getPosition(): Future[List[Int]] = session.request[List[Int]]("window_get_position", this)
  def getTabpage(): Future[Tabpage] = session.request[Tabpage]("window_get_tabpage", this)
  def isValid(): Future[Boolean] = session.request[Boolean]("window_is_valid", this)
}

class Tabpage(val session: Session, val data: Array[Byte]) extends TypeBase {
  def getWindows(): Future[List[Window]] = session.request[List[Window]]("tabpage_get_windows", this)
  def getVar(name: String): Future[Object] = session.request[Object]("tabpage_get_var", this, name)
  def setVar(name: String, value: Object): Future[Object] =
    session.request[Object]("tabpage_set_var", this, name, value)
  def delVar(name: String): Future[Object] = session.request[Object]("tabpage_del_var", this, name)
  def getWindow(): Future[Window] = session.request[Window]("tabpage_get_window", this)
  def isValid(): Future[Boolean] = session.request[Boolean]("tabpage_is_valid", this)
}
