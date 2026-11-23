package com.friendscircle.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF07090F)
val Violet = Color(0xFF7C3AED)
val Indigo = Color(0xFF4F46E5)
val Teal = Color(0xFF2DD4BF)
val Muted = Color(0xFF9AA4B8)
val TextC = Color(0xFFE6EAF2)
val Lilac = Color(0xFFC4B5FD)

data class Chat(val ini: String, val name: String, val last: String, val time: String, val unread: Int = 0, val typing: Boolean = false)
data class Msg(val text: String, val mine: Boolean, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent { MaterialTheme(colorScheme = darkColorScheme()) { App() } }
    }
}

fun Modifier.glass(r: Int = 22): Modifier {
    val s = RoundedCornerShape(r.dp)
    return this.clip(s)
        .background(Brush.verticalGradient(listOf(Color.White.copy(.11f), Color.White.copy(.04f))))
        .border(1.dp, Color.White.copy(.14f), s)
}

val AccentBrush get() = Brush.linearGradient(listOf(Violet, Indigo))

@Composable
fun App() {
    var tab by remember { mutableIntStateOf(0) }
    var open by remember { mutableStateOf<Chat?>(null) }
    Surface(Modifier.fillMaxSize(), color = Ink, contentColor = TextC) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(listOf(Violet.copy(.45f), Color.Transparent), center = Offset(150f, 0f), radius = 1100f)
            )
        ) {
            val c = open
            if (c != null) {
                ChatScreen(c) { open = null }
            } else {
                Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
                    Box(Modifier.weight(1f)) {
                        when (tab) {
                            0 -> ChatsScreen { open = it }
                            1 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Rooms soon", color = Muted) }
                            else -> ProfileScreen()
                        }
                    }
                    BottomNav(tab) { tab = it }
                }
            }
        }
    }
}

@Composable
fun Title(t: String, m: Modifier = Modifier) = Text(
    t, m,
    style = TextStyle(brush = Brush.horizontalGradient(listOf(Color.White, Lilac)), fontSize = 28.sp, fontWeight = FontWeight.Bold)
)

@Composable
fun IconBtn(icon: ImageVector, onClick: () -> Unit = {}) {
    Box(Modifier.size(44.dp).glass(16).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = TextC)
    }
}

@Composable
fun Avatar(ini: String, size: Int, online: Boolean = false) {
    Box {
        Box(
            Modifier.size(size.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(Violet.copy(.5f), Indigo.copy(.25f)))),
            contentAlignment = Alignment.Center
        ) { Text(ini, color = Lilac, fontWeight = FontWeight.Bold, fontSize = (size / 3.4).sp) }
        if (online) Box(
            Modifier.align(Alignment.BottomEnd).size(14.dp).clip(CircleShape).background(Teal).border(3.dp, Ink, CircleShape)
        )
    }
}

@Composable
fun ChatsScreen(onOpen: (Chat) -> Unit) {
    val chats = listOf(
        Chat("GR", "Global Room", "Rana: kal ki plan?", "10:42", 2),
        Chat("SU", "Sumi", "typing...", "10:40", 0, true),
        Chat("AR", "Arif", "Ok, dekhchi", "09:15"),
        Chat("TG", "Trip Gang", "Tithi: photo gulo pathao", "Yesterday", 2),
        Chat("MO", "Mou", "Thik ache", "Mon")
    )
    Column {
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Title("Friends Circle", Modifier.weight(1f))
            IconBtn(Icons.Default.Search)
            Spacer(Modifier.width(8.dp))
            IconBtn(Icons.Default.Add)
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(listOf("AR" to "Arif", "SU" to "Sumi", "RA" to "Rana", "TI" to "Tithi", "MO" to "Mou")) { (i, n) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.border(2.dp, Brush.linearGradient(listOf(Violet, Teal)), CircleShape).padding(4.dp)) { Avatar(i, 52) }
                    Text(n, fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
            items(chats) { c ->
                Row(
                    Modifier.fillMaxWidth().glass(22).clickable { onOpen(c) }.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(c.ini, 52, true)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(c.last, color = if (c.typing) Teal else Muted, fontSize = 14.sp, maxLines = 1)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(c.time, color = Muted, fontSize = 12.sp)
                        if (c.unread > 0) Box(
                            Modifier.padding(top = 6.dp).defaultMinSize(22.dp, 22.dp).clip(CircleShape).background(Violet),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c.unread.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Bubble(m: Msg) {
    Box(Modifier.fillMaxWidth(), contentAlignment = if (m.mine) Alignment.CenterEnd else Alignment.CenterStart) {
        val shape = RoundedCornerShape(18.dp, 18.dp, if (m.mine) 6.dp else 18.dp, if (m.mine) 18.dp else 6.dp)
        val fill = if (m.mine) AccentBrush else Brush.verticalGradient(listOf(Color.White.copy(.11f), Color.White.copy(.04f)))
        Column(Modifier.widthIn(max = 280.dp).clip(shape).background(fill).padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(m.text, fontSize = 15.sp, color = if (m.mine) Color.White else TextC)
            Text(
                m.time, fontSize = 11.sp, color = if (m.mine) Color.White.copy(.8f) else Muted,
                modifier = Modifier.padding(top = 4.dp).align(Alignment.End)
            )
        }
    }
}

@Composable
fun ChatScreen(chat: Chat, onBack: () -> Unit) {
    val msgs = remember {
        mutableStateListOf(
            Msg("Ki khobor? Ajke ghurte jabi?", false, "10:30"),
            Msg("Haa, bikel e jabo. Rana ke-o bol", true, "10:34"),
            Msg("Ami group-e invite code pathacchi", true, "10:36"),
            Msg("Perfect, ami ekhon join korchi", false, "10:38")
        )
    }
    var text by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBtn(Icons.AutoMirrored.Filled.ArrowBack, onBack)
            Spacer(Modifier.width(12.dp))
            Avatar(chat.ini, 44)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(chat.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("online", color = Teal, fontSize = 13.sp)
            }
            IconBtn(Icons.Default.MoreVert)
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
            items(msgs) { Bubble(it) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(48.dp).glass(24).padding(horizontal = 18.dp), contentAlignment = Alignment.CenterStart) {
                if (text.isEmpty()) Text("Message", color = Muted, fontSize = 15.sp)
                BasicTextField(
                    text, { text = it }, Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = TextC, fontSize = 15.sp), cursorBrush = SolidColor(Teal), singleLine = true
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(48.dp).shadow(12.dp, CircleShape, ambientColor = Violet, spotColor = Violet).clip(CircleShape)
                    .background(AccentBrush)
                    .clickable { if (text.isNotBlank()) { msgs.add(Msg(text.trim(), true, "now")); text = "" } },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White) }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ProfileScreen() {
    val clip = LocalClipboardManager.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        Title("Profile", Modifier.align(Alignment.Start))
        Spacer(Modifier.height(24.dp))
        Box(Modifier.border(4.dp, Brush.linearGradient(listOf(Violet, Teal)), CircleShape).padding(8.dp)) { Avatar("YN", 104) }
        Spacer(Modifier.height(12.dp))
        Text("Your Name", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("@username", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        Text("Bio ekhane lekho. Friends-der sathe adda.", color = Color(0xFFC9D1E0), fontSize = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 280.dp))
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(16.dp)).background(AccentBrush).clickable { },
            contentAlignment = Alignment.Center
        ) { Text("Edit profile", fontWeight = FontWeight.SemiBold, color = Color.White) }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().glass(20).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Your invite code", fontSize = 12.sp, color = Muted)
                Text("FC-7K2Q", fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
            }
            Box(
                Modifier.height(44.dp).clip(RoundedCornerShape(14.dp)).background(Teal).clickable { clip.setText(AnnotatedString("FC-7K2Q")) }.padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) { Text("Copy", color = Color(0xFF04211D), fontWeight = FontWeight.Bold, fontSize = 14.sp) }
        }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().glass(20).padding(horizontal = 16.dp)) {
            listOf("Notifications", "Privacy", "Theme").forEachIndexed { i, t ->
                if (i > 0) HorizontalDivider(color = Color.White.copy(.08f))
                Row(Modifier.fillMaxWidth().height(52.dp).clickable { }, verticalAlignment = Alignment.CenterVertically) {
                    Text(t, Modifier.weight(1f), fontSize = 15.sp)
                    Text(">", color = Muted)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun BottomNav(sel: Int, onSel: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 12.dp).navigationBarsPadding().glass(30).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf("Chats", "Rooms", "Profile").forEachIndexed { i, t ->
            val bg = if (i == sel) Brush.linearGradient(listOf(Violet.copy(.5f), Indigo.copy(.28f)))
            else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            Box(
                Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(22.dp)).background(bg).clickable { onSel(i) },
                contentAlignment = Alignment.Center
            ) { Text(t, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (i == sel) Color.White else Muted) }
        }
    }
}
