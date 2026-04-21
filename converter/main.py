import logging
import click
from rich.console import Console
from rich.logging import RichHandler

from .analyzer import PatternAnalyzer
from .converter import EjbConverter

logging.basicConfig(
    level=logging.INFO,
    format="%(message)s",
    handlers=[RichHandler(rich_tracebacks=True)],
)
console = Console()


@click.group()
def cli():
    """EJB → Spring Java 변환 도구"""


@cli.command()
def learn():
    """samples/ 폴더의 AS-IS/TO-BE 쌍으로 변환 패턴을 학습합니다."""
    console.print("[bold cyan]패턴 학습 시작...[/]")
    analyzer = PatternAnalyzer()
    patterns = analyzer.analyze()
    console.print(f"[bold green]학습 완료! 패턴 항목 수: {len(patterns)}[/]")


@cli.command()
@click.option("--relearn", is_flag=True, help="패턴 캐시를 무시하고 재학습")
def convert(relearn: bool):
    """input/ 폴더의 EJB 파일을 Spring 코드로 변환합니다."""
    console.print("[bold cyan]변환 시작...[/]")
    ejb_converter = EjbConverter()
    converted = ejb_converter.convert_all(force_relearn=relearn)
    console.print(f"[bold green]변환 완료! {len(converted)}개 파일 → output/[/]")
    for path in converted:
        console.print(f"  [green]✓[/] {path}")


if __name__ == "__main__":
    cli()
